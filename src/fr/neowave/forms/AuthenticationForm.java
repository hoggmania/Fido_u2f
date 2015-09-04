package fr.neowave.forms;


import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Options;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.messages.Messages;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Formulaire d'authentification sans U2F
 */
public class AuthenticationForm extends Form{

    User user = new User();

    /**
     * Authentifie un utilisateur sans U2F
     * @param request HttpServletRequest
     */
    public void startAuthentication(HttpServletRequest request){
        try {
            if(canAuthenticate(request)){
                user.setRegistrations(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(user.getUsername()));
                if(user.getRegistrations() == null || user.getRegistrations().isEmpty()) {
                    if(!user.getUsername().equals("admin")){
                        request.getSession().setAttribute("u2fAuthenticated", false);
                    }
                    request.getSession().setAttribute("hasKey", false);
                    request.getSession().setAttribute("username", user.getUsername());
                    Options options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
                    request.getSession().setMaxInactiveInterval(options.getSessionInactiveExpirationTime().intValue());

                } else {
                    if(user.getUsername().equals("admin")){
                        request.getSession().setAttribute("tempAdmin", user.getUsername());
                    }
                    else{

                        request.getSession().setAttribute("u2fAuthenticated", false);
                        request.getSession().setAttribute("username", user.getUsername());
                        Options options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
                        request.getSession().setMaxInactiveInterval(options.getSessionInactiveExpirationTime().intValue());
                    }
                    request.getSession().setAttribute("hasKey", true);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | ClassNotFoundException |java.text.ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Vérifie si l'utilisateur peut s'authentifier
     * @param request HttpServletRequest
     * @return Boolean
     * @throws NoSuchAlgorithmException
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     */
    private Boolean canAuthenticate(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, IOException, ClassNotFoundException, java.text.ParseException {

        return (areValidParameters(request) && nothingSuspended());

    }

    /**
     * Vérifie que les paramètres, càd le nom d'utilisateur et le mot de passe sont valides
     * @param request HttpServletRequest
     * @return Boolean
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private Boolean areValidParameters(HttpServletRequest request) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username.trim();
        password = password.trim();

        if (!username.matches("[a-zA-Z0-9_@.]*")) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.AUTH_USERNAME_WRONG_CHARACTERS);
            return false;
        }
        else if (!(username.equals("admin") && request.getServletPath().equals("/adminAuthentication")) && (username.length() > 32 || username.length() < 4)) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.AUTH_USERNAME_WRONG_LENGTH);
            return false;
        }
        else if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.PASSWORD_ERR.toString(), Messages.AUTH_PASSWORD_WRONG_CHARACTERS);
            return false;
        }
        else if (!(username.equals("admin") && request.getServletPath().equals("/adminAuthentication")) && (password.length() > 32 || password.length() < 4)) {
            this.setError(FormErrors.PASSWORD_ERR.toString(), Messages.AUTH_PASSWORD_WRONG_LENGTH);
            return false;
        }
        else if (DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(username) == null) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.AUTH_USERNAME_NOT_FOUND);
            return false;
        }

        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.user = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(username);

            String pass = Base64.encode(md5.digest());
            if(!this.user.getPassword().equals(pass)){
                this.setError(FormErrors.PASSWORD_ERR.toString(), Messages.AUTH_WRONG_PASSWORD);
                return false;
            }
            else{
                return true;
            }
        }
    }

    /**
     * Renvoie false si l'utilisateur est suspendu
     * @return Boolean
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws java.text.ParseException
     */
    private Boolean nothingSuspended() throws SQLException, IOException, ClassNotFoundException, java.text.ParseException {
        if(user.getSuspended()){
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.AUTH_SUSPENDED_ACCOUNT);
            return false;
        }
        else{
            return true;
        }
    }

}

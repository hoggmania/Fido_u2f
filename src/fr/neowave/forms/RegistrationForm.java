package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Options;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.messages.Messages;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


public class RegistrationForm extends Form{


    private User user = new User();


    public void register(HttpServletRequest request){
        try {
            if(canRegister(request)){
                DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().create(user);
                if(request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")){
                    request.getSession().setAttribute("username", user.getUsername());
                    request.getSession().setAttribute("hasKey", false);
                    request.getSession().setAttribute("u2fAuthenticated", false);
                    Options options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
                    request.getSession().setMaxInactiveInterval(options.getSessionInactiveExpirationTime().intValue());
                }

            }
        } catch (SQLException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

    }

    private Boolean canRegister(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, UnsupportedEncodingException {

        Options options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
        if ((request.getSession().getAttribute("username") == null || !request.getSession().getAttribute("username").equals("admin")) && !options.getUserCreateAccount()) {
            this.setError("option", Messages.REG_CANT_CREATE_ACCOUNT);
            return false;
        }
        else return areValidParameters(request);
    }


    /**
     * @param request HttpServletRequest
     * @return Boolean
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    private Boolean areValidParameters(HttpServletRequest request) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordConfirmation = request.getParameter("passwordConfirmation");

        username = username.trim();
        password = password.trim();
        passwordConfirmation = passwordConfirmation.trim();

        if(!username.matches("[a-zA-Z0-9_@.]*")) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.REG_USERNAME_WRONG_CHARACTERS);
            return false;
        }
        else if (username.length() > 32 || username.length() < 4) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.REG_USERNAME_WRONG_LENGTH);
            return false;
        }
        else if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.REG_PASSWORD_WRONG_CHARACTERS);
            return false;
        }
        else if (password.length() > 32 || password.length() < 4) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.REG_PASSWORD_WRONG_SIZE);
            return false;
        }
        else if (!password.equals(passwordConfirmation)) {
            this.setError(FormErrors.PASSWORD_ERR.toString(), Messages.REG_PASSWORDS_DONT_MATCH);
            return false;
        }
        else if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(username) != null) {
            this.setError(FormErrors.USERNAME_ERR.toString(), Messages.REG_USERNAME_ALREADY_EXISTS);
            return false;
        }
        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.user.setPassword(Base64.encode(md5.digest()));
            this.user.setUsername(username);
            this.user.setSuspended(false);

            return true;
        }

    }


}

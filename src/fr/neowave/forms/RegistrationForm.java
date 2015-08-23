package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Options;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


public class RegistrationForm extends Form{


    private User user = new User();


    public User register(HttpServletRequest request){
        try {
            if(canRegister(request)){
                DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().create(user);
            }
        } catch (SQLException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

        return user;
    }

    private Boolean canRegister(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, UnsupportedEncodingException {

        Options options = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().getOptions();
        if(areValidParameters(request)){
            return true;
        }
        else if (request.getSession().getAttribute("username") != null &&
                request.getSession().getAttribute("username").equals("admin")
                && !options.getUserCreateAccount()){
            this.setError("default", "Only admin can create account");
            return false;
        }
        else{
            return false;
        }
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
            this.setError(FormErrors.USERNAME_ERR.toString(), "Allowed characters are a-z A-Z 0-9 . _ @.");
            return false;
        }
        else if (username.length() > 32 || username.length() < 6) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Username size must be between 6 and 32.");
            return false;
        }
        else if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Allowed characters are a-z A-Z 0-9.");
            return false;
        }
        else if (password.length() > 32 || password.length() < 6) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Password size must be between 6 and 32.");
            return false;
        }
        else if (!password.equals(passwordConfirmation)) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Passwords don't match.");
            return false;
        }
        else if(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) != null) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "User already exists.");
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

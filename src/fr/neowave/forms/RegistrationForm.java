package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.forms.Exceptions.PasswordException;
import fr.neowave.forms.Exceptions.UsernameException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;


public class RegistrationForm {

    private String username = null;
    private String password = null;

    private FormResponse formResponse = null;


    public User register(HttpServletRequest request){
        formResponse = new FormResponse();
        User user = null;
        try {
            if(areValidParameters(request)){
                user = createUser();
                DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().create(user);
                formResponse.setMessage("You have been registered");
            }
        } catch (SQLException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        } catch (UsernameException e) {
            formResponse.setError(FormErrors.USERNAME_ERR.toString(), e.getMessage());
        } catch (PasswordException e) {
            formResponse.setError(FormErrors.PASSWORD_ERR.toString(), e.getMessage());
        }

        return user;
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

        if(!username.matches("[a-zA-Z0-9_@.]*")) throw new UsernameException("Allowed characters are a-z A-Z 0-9 . _ @.");
        else if (username.length() > 32 || username.length() < 6) throw new UsernameException("Username size must be between 6 and 32.");
        else if (!password.matches("[a-zA-Z0-9]*")) throw new PasswordException("Allowed characters are a-z A-Z 0-9.");
        else if (password.length() > 32 || password.length() < 6) throw new PasswordException("Password size must be between 6 and 32.");
        else if (!password.equals(passwordConfirmation)) throw new PasswordException("Passwords don't match.");
        else if(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) != null) throw new UsernameException("User already exists.");
        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.password = Base64.encode(md5.digest());
            this.username = username;

            return true;
        }

    }

    private User createUser(){
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setSuspended(false);

        return user;
    }

    public FormResponse getFormResponse() {
        return formResponse;
    }

}

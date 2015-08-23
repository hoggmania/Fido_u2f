package fr.neowave.forms;


import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class AuthenticationForm extends Form{

    User user = new User();

    public AuthenticationForm() {
       super();
    }

    public void startAuthentication(HttpServletRequest request){
        try {
            if(canAuthenticate(request)){
                if(user.getRegistrations() == null) {
                    request.getSession().setAttribute("hasKey", false);
                    request.getSession().setAttribute("username", user.getUsername());
                } else {
                    request.getSession().setAttribute("hasKey", true);
                    if(user.getUsername().equals("admin")) request.getSession().setAttribute("tempAdmin", user.getUsername());
                }


                this.setMessage("No error");
            }
        } catch (SQLException | NoSuchAlgorithmException | ClassNotFoundException |java.text.ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    private Boolean canAuthenticate(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, IOException, ClassNotFoundException, java.text.ParseException {

        return areValidParameters(request) && nothingSuspended();

    }

    private Boolean areValidParameters(HttpServletRequest request) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username.trim();
        password = password.trim();

        if (!username.matches("[a-zA-Z0-9_@.]*")) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Username not found");
            return false;
        }
        else if ((username.length() > 32 || username.length() < 4)) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "Username not found");
            return false;
        }
        else if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.PASSWORD_ERR.toString(), "Wrong password");
            return false;
        }
        else if ((password.length() > 32 || password.length() < 4)) {
            this.setError(FormErrors.PASSWORD_ERR.toString(), "Wrong password");
            return false;
        }
        else if (DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) == null) {
            this.setError(FormErrors.USERNAME_ERR.toString(), "User not found.");
            return false;
        }

        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.user = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username);

            String pass = Base64.encode(md5.digest());
            System.out.println(this.user.getPassword());
            if(!this.user.getPassword().equals(pass)){
                this.setError(FormErrors.PASSWORD_ERR.toString(), "Wrong password");
                return false;
            }
            else{
                return true;
            }
        }
    }

    private Boolean nothingSuspended() throws SQLException, IOException, ClassNotFoundException, java.text.ParseException {
        if(user.getSuspended()){
            this.setError(FormErrors.USERNAME_ERR.toString(), "Your account has been suspended by admin");
            return false;
        }
        else{
            List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(user.getUsername());

            if(registrations.isEmpty()) return true;
            registrations.stream().filter(Registration::getSuspended).forEach(registrations::remove);

            if (registrations.isEmpty()) {
                this.setError(FormErrors.DEFAULT_ERR.toString(), "Your tokens are all suspended");
                return false;
            }
            user.setRegistrations(registrations);
            return true;
        }
    }

}

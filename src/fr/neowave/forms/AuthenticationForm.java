package fr.neowave.forms;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.forms.Exceptions.PasswordException;
import fr.neowave.forms.Exceptions.SuspendedException;
import fr.neowave.forms.Exceptions.UsernameException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import u2f.U2F;
import u2f.data.DeviceRegistration;
import u2f.data.messages.*;
import u2f.exceptions.DeviceCompromisedException;
import u2f.exceptions.NoEligableDevicesException;
import u2f.exceptions.U2fBadConfigurationException;
import u2f.exceptions.U2fBadInputException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationForm {

    User user = null;
    List<Registration> registrations = null;
    FormResponse formResponse = null;

    public User startAuthentication(HttpServletRequest request){
        formResponse = new FormResponse();
        try {
            if(canAuthenticate(request)){
                user.setRegistrations(registrations);
            }
        } catch (SQLException | NoSuchAlgorithmException | ClassNotFoundException |java.text.ParseException | IOException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

        return user;
    }



    private Boolean canAuthenticate(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, IOException, ClassNotFoundException, java.text.ParseException {


        if(areValidParameters(request) && nothingSuspended()){
            return true;
        }
        else{
            return false;
        }

    }

    private Boolean areValidParameters(HttpServletRequest request) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username.trim();
        password = password.trim();

        if (!username.matches("[a-zA-Z0-9_@.]*")) {
            formResponse.setError(FormErrors.USERNAME_ERR.toString(),"Username not found");
            return false;
        }
        else if (username.equals("admin") || (username.length() > 32 || username.length() < 6)) {
            formResponse.setError(FormErrors.USERNAME_ERR.toString(),"Username not found");
            return false;
        }
        else if (!password.matches("[a-zA-Z0-9]*")) {
            formResponse.setError(FormErrors.PASSWORD_ERR.toString(),"Wrong password");
            return false;
        }
        else if (password.length() > 32 || password.length() < 6) {
            formResponse.setError(FormErrors.PASSWORD_ERR.toString(),"Wrong password");
            return false;
        }
        else if (DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) == null) {
            formResponse.setError(FormErrors.USERNAME_ERR.toString(),"User not found.");
            return false;
        }

        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.user = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username);

            if(!this.user.getPassword().equals(Base64.encode(md5.digest()))){
                formResponse.setError(FormErrors.PASSWORD_ERR.toString(),"Wrong password");
                return false;
            }
            else{
                return true;
            }
        }
    }

    private Boolean nothingSuspended() throws SQLException, IOException, ClassNotFoundException, java.text.ParseException {
        if(user.getSuspended()){
            formResponse.setError(FormErrors.USERNAME_ERR.toString(),"Your account has been suspended by admin");
            return false;
        }
        else{
            registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(user.getUsername());

            if(registrations.isEmpty()) return true;
            registrations.stream().filter(Registration::getSuspended).forEach(registrations::remove);

            if (registrations.isEmpty()) {
                formResponse.setError(FormErrors.DEFAULT_ERR.toString(),"Your tokens are all suspended");
                return false;
            }

            return true;
        }
    }

    public FormResponse getFormResponse() {
        return formResponse;
    }

}

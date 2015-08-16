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
import u2f.data.messages.AuthenticateRequest;
import u2f.data.messages.AuthenticateRequestData;
import u2f.data.messages.RegisterRequestData;
import u2f.data.messages.RegisterResponse;
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
        } catch (SQLException | NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

        return user;
    }

    public void doAuthentication(HttpServletRequest request){
        formResponse = new FormResponse();
        U2F u2f = new U2F();
        try {

            JSONObject temp = (JSONObject) new JSONParser().parse((String) request.getSession().getAttribute("challengeRequest"));

            RegisterRequestData registerRequestData =  RegisterRequestData.fromJson("{\"registerRequests\" :["
                    .concat(temp.get("registerRequests").toString())
                    .concat("], \"authenticateRequests\" :[")
                    .concat(temp.get("authenticateRequests").toString().equals("{}")? "":temp.get("authenticateRequests").toString() )
                    .concat("]}"));


            RegisterResponse registerResponse = RegisterResponse.fromJson(request.getParameter("response"));


            DeviceRegistration deviceRegistration = u2f.finishRegistration(registerRequestData, registerResponse);

            Registration registration = new Registration(deviceRegistration.getKeyHandle(), deviceRegistration.getPublicKey(),
                    deviceRegistration.getAttestationCertificate(), deviceRegistration.getCounter());


            Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            registration.setTimestamp(new Date(now.getTime()));
            registration.setSuspended(false);
            registration.setUsername((String) request.getSession().getAttribute("username"));
            registration.setHostname(request.getRequestURI());

            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().create(registration);
            formResponse.setMessage("Key has been added");

        } catch (ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | IOException | CertificateException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
            e.printStackTrace();
        }


    }

    public void authChallenge(HttpServletRequest request) {
        URI uri;
        U2F u2f = new U2F();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{\"AuthenticateRequest\" :{\"");
        try {
            uri = new URI(request.getRequestURL().toString());
            List<AuthenticateRequest> authenticateRequests = u2f.startAuthentication(uri.getScheme().concat("://").concat(uri.getHost()), registrations)
                    .getAuthenticateRequests();

            if(!registrations.isEmpty()){
                for(Registration registration : registrations){
                    stringBuilder.append("\"");
                    stringBuilder.append(registration.hashCode());
                    stringBuilder.append("\":\"");
                    stringBuilder.append(registration.toJson());
                    stringBuilder.append("\",");
                }


                stringBuilder.deleteCharAt(stringBuilder.length()-1);
            }

            stringBuilder.append("}");
            formResponse.setMessage(stringBuilder.toString());

        } catch (URISyntaxException | NoEligableDevicesException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

    }

    private Boolean canAuthenticate(HttpServletRequest request) throws NoSuchAlgorithmException, SQLException, IOException, ClassNotFoundException {


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
        else if (username.length() > 32 || username.length() < 6) {
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

    private Boolean nothingSuspended() throws SQLException, IOException, ClassNotFoundException {
        if(user.getSuspended()){
            formResponse.setError(FormErrors.USERNAME_ERR.toString(),"Your account has been suspended by admin");
            return false;
        }
        else{
            registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(user.getUsername());

            if(registrations.isEmpty()) return true;
            System.err.println(registrations);
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

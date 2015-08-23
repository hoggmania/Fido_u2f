package fr.neowave.forms;

import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import u2f.U2F;
import u2f.data.DeviceRegistration;
import u2f.data.messages.AuthenticateRequest;
import u2f.data.messages.RegisterRequest;
import u2f.data.messages.RegisterRequestData;
import u2f.data.messages.RegisterResponse;
import u2f.exceptions.U2fBadConfigurationException;
import u2f.exceptions.U2fBadInputException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


public class U2fRegistrationForm extends Form {

    public void startU2fRegistration(HttpServletRequest request){
        U2F u2f = new U2F();
        URI uri;
        String username;
        Options options;


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"registerRequests\" :");
        try {
            options = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().getOptions();
            if(!request.getSession().getAttribute("username").equals("admin") || (request.getSession().getAttribute("username").equals("admin"))){
                username = String.valueOf(request.getSession().getAttribute("username"));
                if(!options.getUsersAddNewTokens() && !request.getSession().getAttribute("username").equals("admin") && !(Boolean) request.getSession().getAttribute("hasKey")){
                    throw new Exception("Option 6) a) activated, you can't add token");
                }

                if(!options.getUsersRegisterTheirOwnFirstToken() && !request.getSession().getAttribute("username").equals("admin") && !(Boolean) request.getSession().getAttribute("hasKey")){
                    throw new Exception("Option 5) a) activated, you can't add your first token");
                }
            }
            else{
                username = request.getParameter("username");
            }
            uri = new URI(request.getRequestURL().toString());

            List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(username);

            List<DeviceRegistration> deviceRegistrations = registrations.stream().map(registration -> new DeviceRegistration(registration.getKeyHandle(), registration.getPublicKey(), registration.getCertificate(),
                    registration.getCounter())).collect(Collectors.toList());

            RegisterRequestData registerRequestData = u2f.startRegistration(uri.getScheme().concat("://").concat(uri.getHost()).concat(":")
                    .concat(String.valueOf(uri.getPort())), deviceRegistrations);
            List<RegisterRequest> registerRequests = registerRequestData.getRegisterRequests();
            stringBuilder.append(registerRequests.get(0).toJson());


            List<AuthenticateRequest> authenticateRequests = registerRequestData.getAuthenticateRequests();
            stringBuilder.append(", \"authenticateRequests\" : [");
            if(!authenticateRequests.isEmpty()){
                for(AuthenticateRequest authenticateRequest : authenticateRequests)
                    stringBuilder.append(authenticateRequest.toJson().concat(","));
                stringBuilder.deleteCharAt(stringBuilder.length()-1);

            }
            stringBuilder.append("]}");
            request.getSession().setAttribute("registrationChallenge", stringBuilder.toString());
            this.setMessage("Please, connect your u2f token");

        } catch (Exception e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void doU2fRegistration(HttpServletRequest request){
        U2F u2f = new U2F();
        String registrationChallenge = String.valueOf(request.getSession().getAttribute("registrationChallenge"));
        request.getSession().removeAttribute("registrationChallenge");
        String username;
        if(!request.getSession().getAttribute("username").equals("admin") || (request.getSession().getAttribute("username").equals("admin") && !(Boolean) request.getSession().getAttribute("hasKey"))){
            username = String.valueOf(request.getSession().getAttribute("username"));
        }
        else{
            username = request.getParameter("username");
        }
        try {

            JSONObject temp = (JSONObject) new JSONParser().parse(registrationChallenge);

            RegisterRequestData registerRequestData =  RegisterRequestData.fromJson("{\"registerRequests\" :["
                    .concat(temp.get("registerRequests").toString())
                    .concat("], \"authenticateRequests\" :")
                    .concat(temp.get("authenticateRequests").toString().equals("[]") ? "[]" : temp.get("authenticateRequests").toString())
                    .concat("}"));



            RegisterResponse registerResponse = RegisterResponse.fromJson(request.getParameter("response"));


            DeviceRegistration deviceRegistration = u2f.finishRegistration(registerRequestData, registerResponse);

            Registration registration = new Registration();
            registration.setKeyHandle(deviceRegistration.getKeyHandle());
            registration.setPublicKey(deviceRegistration.getPublicKey());
            registration.setCertificate(deviceRegistration.getAttestationCertificate());
            registration.setCounter(deviceRegistration.getCounter());

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            registration.setTimestamp(dateFormat.format(date));
            registration.setSuspended(false);
            registration.setUsername(username);
            registration.setHostname(request.getRequestURI());

            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().create(registration);
            request.getSession().setAttribute("hasKey", true);
            this.setMessage("Key has been added");

        } catch (ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | IOException | CertificateException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }


    }
}

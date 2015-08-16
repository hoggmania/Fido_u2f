package fr.neowave.forms;

import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import u2f.U2F;
import u2f.data.DeviceRegistration;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class AddKeyForm {

    private FormResponse formResponse = null;

    public void regChallenge(HttpServletRequest request){
        formResponse = new FormResponse();
        U2F u2f = new U2F();
        URI uri;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"registerRequests\" :");
        try {
            uri = new URI(request.getRequestURL().toString());

            List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao()
                    .list(String.valueOf(request.getSession().getAttribute("username")));

            List<RegisterRequest> registerRequests = u2f.startRegistration(uri.getScheme().concat("://").concat(uri.getHost()).concat(":")
                    .concat(String.valueOf(uri.getPort())), registrations)
                    .getRegisterRequests();
            stringBuilder.append(registerRequests.get(0).toJson());
            stringBuilder.append(", \"authenticateRequests\" : {");
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

            stringBuilder.append("}}");
            formResponse.setMessage(stringBuilder.toString());

        } catch (URISyntaxException | SQLException | IOException | ClassNotFoundException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void doRegistration(HttpServletRequest request){
        formResponse = new FormResponse();
        U2F u2f = new U2F();
        try {

            JSONObject temp = (JSONObject) new JSONParser().parse((String) request.getSession().getAttribute("challengeRequest"));

            RegisterRequestData registerRequestData =  RegisterRequestData.fromJson("{\"registerRequests\" :["
                    .concat(temp.get("registerRequests").toString())
                    .concat("], \"authenticateRequests\" :[")
                    .concat(temp.get("authenticateRequests").toString().equals("{}")? "":temp.get("authenticateRequests").toString() )
                    .concat("]}"));

            System.err.println(request.getParameter("response"));
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

    public FormResponse getFormResponse() {
        return formResponse;
    }
}

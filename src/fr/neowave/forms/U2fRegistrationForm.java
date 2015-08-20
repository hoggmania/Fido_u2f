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
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class U2fRegistrationForm {

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
                    .list(String.valueOf(request.getSession().getAttribute("username").equals("admin") ? request.getParameter("username")
            :request.getSession().getAttribute("username")));

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

            formResponse.setMessage(stringBuilder.toString());

        } catch (URISyntaxException | SQLException | IOException | java.text.ParseException | ClassNotFoundException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void doRegistration(HttpServletRequest request){
        formResponse = new FormResponse();
        U2F u2f = new U2F();
        try {

            JSONObject temp = (JSONObject) new JSONParser().parse((String) request.getSession().getAttribute("challengeRequest"));
            System.out.println(temp);
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
            registration.setUsername( request.getSession().getAttribute("username").equals("admin") ? request.getParameter("username")
                    : (String) request.getSession().getAttribute("username"));
            registration.setHostname(request.getRequestURI());

            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().create(registration);
            formResponse.setMessage("Key has been added");

        } catch (ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | IOException | CertificateException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }


    }

    public FormResponse getFormResponse() {
        return formResponse;
    }
}

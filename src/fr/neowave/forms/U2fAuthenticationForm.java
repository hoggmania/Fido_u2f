package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import u2f.U2F;
import u2f.data.DeviceRegistration;
import u2f.data.messages.AuthenticateRequest;
import u2f.data.messages.AuthenticateRequestData;
import u2f.data.messages.AuthenticateResponse;
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
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class U2fAuthenticationForm {

    FormResponse formResponse = null;

    public void startU2fAuthentication(HttpServletRequest request, List<Registration> registrations){
        URI uri;
        U2F u2f = new U2F();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            uri = new URI(request.getRequestURL().toString());


            List<DeviceRegistration> deviceRegistrations = registrations.stream().map(registration -> new DeviceRegistration(registration.getKeyHandle(), registration.getPublicKey(), registration.getCertificate(),
                    registration.getCounter())).collect(Collectors.toList());

            List<AuthenticateRequest> authenticateRequests = u2f.startAuthentication(uri.getScheme().concat("://").concat(uri.getHost())
                    .concat(":").concat(String.valueOf(uri.getPort())), deviceRegistrations)
                    .getAuthenticateRequests();


            stringBuilder.append("{\"authenticateRequests\":[");

            if(!authenticateRequests.isEmpty()){
                for(AuthenticateRequest authenticateRequest : authenticateRequests)
                    stringBuilder.append(authenticateRequest.toJson().concat(","));
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                stringBuilder.append("]");
            }
            stringBuilder.append("}");

            formResponse.setMessage(stringBuilder.toString());

        } catch (URISyntaxException | NoEligableDevicesException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

    }

    public void doAuthentication(HttpServletRequest request){
        formResponse = new FormResponse();
        U2F u2f = new U2F();
        try {

            JSONObject temp = (JSONObject) new JSONParser().parse((String) request.getSession().getAttribute("authenticateRequests"));

            AuthenticateRequestData authenticateRequestData =  AuthenticateRequestData.fromJson("{\"authenticateRequests\":"
                    .concat(temp.get("authenticateRequests").toString().equals("{}") ? "" : temp.get("authenticateRequests").toString())
                    .concat("}"));


            AuthenticateResponse authenticateResponse = AuthenticateResponse.fromJson(request.getParameter("response"));
            List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list((String) request.getSession().getAttribute("tempUsername"));
            List<DeviceRegistration> deviceRegistrations = registrations.stream().map(registration -> new DeviceRegistration(registration.getKeyHandle(), registration.getPublicKey(), registration.getCertificate(),
                    registration.getCounter())).collect(Collectors.toList());

            DeviceRegistration deviceRegistration = u2f.finishAuthentication(authenticateRequestData, authenticateResponse, deviceRegistrations);

            JSONObject counter = (JSONObject) new JSONParser().parse(deviceRegistration.toJson());

            Registration registration = new Registration();

            registration.setKeyHandle(deviceRegistration.getKeyHandle());
            registration.setPublicKey(deviceRegistration.getPublicKey());
            registration.setCertificate(deviceRegistration.getAttestationCertificate());
            registration.setCounter(Long.valueOf(counter.get("counter").toString()));

            registration.setUsername((String) request.getSession().getAttribute("tempUsername"));
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().updateCounter(registration);
            formResponse.setMessage("you are connected");

        } catch (ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | CertificateException | ClassNotFoundException | DeviceCompromisedException | IOException | java.text.ParseException e) {
            formResponse.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage()!=null? e.getMessage() : "Unknown error" );
        }


    }


    public FormResponse getFormResponse() {
        return formResponse;
    }
}

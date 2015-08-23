package fr.neowave.forms;

import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class U2fAuthenticationForm extends Form {

    public void startU2fAuthentication(HttpServletRequest request){
        URI uri;
        U2F u2f = new U2F();
        StringBuilder stringBuilder = new StringBuilder();
        List<Registration> registrations;
        Options options ;
        try {
            registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("tempUsername")));
            options = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().getOptions();
            if (request.getSession().getAttribute("username") == null && options.getAdminReplaceUsersTokens()){
                registrations.addAll(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list("admin"));
            }


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

            request.getSession().setAttribute("authenticationChallenge", stringBuilder.toString());
            request.getSession().setAttribute("timeStamp", System.currentTimeMillis());
            this.setMessage("Please connect your token");

        } catch (URISyntaxException | NoEligableDevicesException | ClassNotFoundException | SQLException | IOException | ParseException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }

    }
    public void doU2fAuthentication(HttpServletRequest request){
        U2F u2f = new U2F();
        String authenticationRequests = String.valueOf(request.getSession().getAttribute("authenticationChallenge"));
        request.getSession().removeAttribute("authenticationChallenge");
        String username = String.valueOf(request.getSession().getAttribute("tempUsername"));
        request.getSession().removeAttribute("tempUsername");
        Long timestamp = Long.valueOf(String.valueOf(request.getSession().getAttribute("timeStamp")));
        request.getSession().removeAttribute("timeStamp");
        try {
            if((System.currentTimeMillis()-timestamp) > 120000) throw new TimeoutException("Request timeout, please try again");

            JSONObject temp = (JSONObject) new JSONParser().parse(authenticationRequests);

            AuthenticateRequestData authenticateRequestData =  AuthenticateRequestData.fromJson("{\"authenticateRequests\":"
                    .concat(temp.get("authenticateRequests").toString().equals("{}") ? "" : temp.get("authenticateRequests").toString())
                    .concat("}"));


            AuthenticateResponse authenticateResponse = AuthenticateResponse.fromJson(request.getParameter("response"));
            List<Registration> registrations = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(username);
            List<DeviceRegistration> deviceRegistrations = registrations.stream().map(registration -> new DeviceRegistration(registration.getKeyHandle(), registration.getPublicKey(), registration.getCertificate(),
                    registration.getCounter())).collect(Collectors.toList());

            DeviceRegistration deviceRegistration = u2f.finishAuthentication(authenticateRequestData, authenticateResponse, deviceRegistrations);

            JSONObject counter = (JSONObject) new JSONParser().parse(deviceRegistration.toJson());

            Registration registration = new Registration();

            registration.setKeyHandle(deviceRegistration.getKeyHandle());
            registration.setPublicKey(deviceRegistration.getPublicKey());
            registration.setCertificate(deviceRegistration.getAttestationCertificate());
            registration.setCounter(Long.valueOf(counter.get("counter").toString()));

            registration.setUsername(username);
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().updateCounter(registration);
            request.getSession().setAttribute("username", username);
            this.setMessage("you are connected");

        } catch (TimeoutException | org.json.simple.parser.ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | CertificateException | ClassNotFoundException | DeviceCompromisedException | IOException | ParseException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage() != null ? e.getMessage() : "Unknown error");
        }


    }
}

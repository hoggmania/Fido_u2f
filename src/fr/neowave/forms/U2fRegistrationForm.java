package fr.neowave.forms;

import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.messages.Messages;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Formulaire d'enregistrement de clé U2F
 */
public class U2fRegistrationForm extends Form {

    /**
     * Commence l'enregistrement de la clé en stockant la requête dans la session
     * @param request HttpServletRequest
     */
    public void startU2fRegistration(HttpServletRequest request){
        U2F u2f = new U2F();
        URI uri;
        String username;
        Options options;


        StringBuilder stringBuilder;

        try {
            options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
            if(request.getSession().getAttribute("username").equals("admin") && request.getSession().getAttribute("hasKey").equals(true)){
                username = request.getParameter("username");
                request.getSession().setAttribute("tempUser", username);
            } else{
                username = String.valueOf(request.getSession().getAttribute("username"));
            }

            if(!options.getUsersAddNewTokens() && !request.getSession().getAttribute("username").equals("admin") && (Boolean) request.getSession().getAttribute("hasKey")){
                this.setError("option", Messages.U2F_REG_CANT_ADD_TOKEN);
            }else if(!options.getUsersRegisterTheirOwnFirstToken() && !request.getSession().getAttribute("username").equals("admin") && !(Boolean) request.getSession().getAttribute("hasKey")){
                this.setError("option", Messages.U2F_REG_CANT_ADD_NEW_TOKEN);
            }else {
                List<User> users = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().list();
                Boolean temp = false;
                for (User user : users){
                    if(user.getUsername().equals(username)) temp = true;
                }
                if(temp){
                    uri = new URI(request.getRequestURL().toString());
                    stringBuilder = new StringBuilder();

                    stringBuilder.append("{\"registerRequests\" :");

                    List<Registration> registrations = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(username);

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
                    request.getSession().setAttribute("timeStamp", System.currentTimeMillis());
                }
                else{
                    throw new RuntimeException("User doesn't exist");
                }
            }
        } catch (RuntimeException | java.text.ParseException | IOException | URISyntaxException | ClassNotFoundException | SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Récupère la requete d'enregistrement de la session et la supprime de celle-ci puis enregistre la clé
     * @param request HttpServletRequest
     */
    public void doU2fRegistration(HttpServletRequest request){
        U2F u2f = new U2F();
        String registrationChallenge = String.valueOf(request.getSession().getAttribute("registrationChallenge"));
        request.getSession().removeAttribute("registrationChallenge");
        String username;
        if(request.getSession().getAttribute("username").equals("admin") && request.getSession().getAttribute("hasKey").equals(true)){
            username = String.valueOf(request.getSession().getAttribute("tempUser"));
            request.getSession().removeAttribute("tempUser");
        } else{
            username = String.valueOf(request.getSession().getAttribute("username"));
        }

        Long timestamp = Long.valueOf(String.valueOf(request.getSession().getAttribute("timeStamp")));
        request.getSession().removeAttribute("timeStamp");
        try {
            if((System.currentTimeMillis()-timestamp) > (DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getRequestValidityTime()*1000)) throw new TimeoutException(Messages.U2F_REG_REQUEST_TIMEOUT);
            JSONObject temp = (JSONObject) new JSONParser().parse(registrationChallenge);

            RegisterRequestData registerRequestData =  RegisterRequestData.fromJson("{\"registerRequests\" :["
                    .concat(temp.get("registerRequests").toString())
                    .concat("], \"authenticateRequests\" :")
                    .concat(temp.get("authenticateRequests").toString().equals("[]") ? "[]" : temp.get("authenticateRequests").toString())
                    .concat("}"));



            RegisterResponse registerResponse = RegisterResponse.fromJson(request.getParameter("response"));

            DeviceRegistration deviceRegistration = u2f.finishRegistration(registerRequestData, registerResponse);

            if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getUsersSeeDetails()){
                this.setMessage(deviceRegistration.toJson());
            }
            this.setMessage("{\"keyHandle\":\"".concat(deviceRegistration.getKeyHandle()).concat("\",\"publicKey\":\"").concat(deviceRegistration.getPublicKey())
                    .concat("\",\"counter\":\"").concat(String.valueOf(deviceRegistration.getCounter())).concat("\",\"certificateFrom\":\"").concat(deviceRegistration.getAttestationCertificate().getIssuerDN().toString()).concat("\"}"));
            X509Certificate cert = deviceRegistration.getAttestationCertificate();

            if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getOnlyNeowave() && !cert.getIssuerDN().getName().contains("Neowave")){
                this.setError("option", Messages.U2F_REG_ONLY_NEOWAVE);
            }
            else{
                if(cert.getIssuerDN().getName().contains("Neowave")){
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    InputStream PropertiesFile = new FileInputStream(getClass().getResource("CA/neowave.pem").getFile());
                    X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(PropertiesFile);
                    cert.verify(certificate.getPublicKey());
                }
                if(cert.getIssuerDN().getName().contains("Plug-up")){
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    InputStream PropertiesFile = new FileInputStream(getClass().getResource("CA/plugup.pem").getFile());
                    X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(PropertiesFile);
                    cert.verify(certificate.getPublicKey());
                }
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

                registration.setHostname(InetAddress.getByName(getClientIpAddress(request)).getHostName());
                DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().create(registration);
                request.getSession().setAttribute("hasKey", true);
                request.getSession().setAttribute("u2fAuthenticated", true);
            }


        } catch (TimeoutException | ParseException | U2fBadInputException | U2fBadConfigurationException | NoSuchFieldException | SQLException | IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }


    }

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}

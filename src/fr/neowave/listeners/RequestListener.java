package fr.neowave.listeners;

import fr.neowave.beans.Logger;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

@WebListener
public class RequestListener implements ServletRequestAttributeListener, ServletRequestListener {



    @Override
    public void attributeAdded(ServletRequestAttributeEvent servletRequestAttributeEvent) {
        Logger logger = new Logger();
        HttpServletRequest request = (HttpServletRequest) servletRequestAttributeEvent.getServletRequest();

        String  userAgent       =   request.getHeader("User-Agent");
        String  user            =   userAgent.toLowerCase();
        String os;
        String browser = "";

        if (userAgent.toLowerCase().contains("windows"))
        {
            os = "Windows";
        } else if(userAgent.toLowerCase().contains("mac"))
        {
            os = "Mac";
        } else if(userAgent.toLowerCase().contains("x11"))
        {
            os = "Unix";
        } else if(userAgent.toLowerCase().contains("android"))
        {
            os = "Android";
        } else if(userAgent.toLowerCase().contains("iphone"))
        {
            os = "IPhone";
        }else{
            os = "UnKnown, More-Info: "+userAgent;
        }


        if (user.contains("msie"))
        {
            String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if ( user.contains("opr") || user.contains("opera"))
        {
            if(user.contains("opera"))
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if(user.contains("opr"))
                browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
        } else if (user.contains("chrome"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))  || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3")) )
        {
            //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
            browser = "Netscape-?";

        } else if (user.contains("firefox"))
        {
            browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if(user.contains("rv"))
        {
            browser="IE";
        } else
        {
            browser = "UnKnown, More-Info: "+userAgent;
        }








        String[] headers = {
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

        String ip = null;
        for (String header : headers) {
            if (request.getHeader(header) != null && request.getHeader(header).length() != 0 && !"unknown".equalsIgnoreCase(request.getHeader(header))) {
                ip = request.getHeader(header);
            }
        }

        if(ip == null){
            ip = request.getRemoteAddr();
        }

        logger.setServerSessionId(request.getSession().getId());
        logger.setMessage("Request");
        logger.setContext(request.getRequestURI());
        logger.setUsername(request.getSession().getAttribute("username") == null ? "User not logged" : String.valueOf(request.getSession().getAttribute("username")));
        logger.setBrowserName(browser.replaceAll("[^A-Za-z]", ""));
        logger.setBrowserVersion(browser.replaceAll("[\\D]", ""));
        logger.setOsName(os);
        logger.setOsVersion(userAgent);
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            request.getSession().setAttribute("details", DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getUsersSeeDetails());
            logger.setIp(ip);
            logger.setReverseName(inetAddress.getHostName());

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        StringBuilder parameters = new StringBuilder();
        parameters.append("{");
        Enumeration<String> enumeration = request.getParameterNames();
        String parameter;
        while(enumeration.hasMoreElements()){
            String temp = enumeration.nextElement();
            if(temp.equals("password")){
                parameter = "Hidden Password";
            }else{
                parameter = request.getParameter(temp);
            }
            parameters.append(temp);
            parameters.append(":");
            parameters.append(parameter);
            parameters.append(",");
        }
        if(parameters.length() > 2) parameters.deleteCharAt(parameters.length()-1);
        parameters.append("}");

        StringBuilder attributes = new StringBuilder();
        StringBuilder errors = new StringBuilder();
        attributes.append("{");
        errors.append("{");

        enumeration = request.getAttributeNames();
        while(enumeration.hasMoreElements()){
            String temp = enumeration.nextElement();

            if(temp.equals("errors")){
                errors.append(temp);
                errors.append(":");
                errors.append(request.getAttribute(temp));
                errors.append(",");
            }else{
                attributes.append(temp);
                attributes.append(":");
                attributes.append(request.getAttribute(temp));
                attributes.append(",");
            }


        }
        if(attributes.length() > 2) attributes.deleteCharAt(attributes.length()-1);
        attributes.append("}");

        if(errors.length() > 2) errors.deleteCharAt(errors.length()-1);
        errors.append("}");


        logger.setRequestParameters(parameters.toString());
        logger.setRequestAttributes(attributes.toString());
        logger.setRequestErrors(errors.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        logger.setDateTimeStart(simpleDateFormat.format(new Date(System.currentTimeMillis())));

        try {
            DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        if(request.getSession().getAttribute("username") != null){
            try {
                request.getSession().setAttribute("hasKey", !DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))).isEmpty());
                request.getSession().setAttribute("tokenTimeout", DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getDelayToPutToken());

            } catch (SQLException | IOException | ParseException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent servletRequestAttributeEvent) {

    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent servletRequestAttributeEvent) {

    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {


    }
}

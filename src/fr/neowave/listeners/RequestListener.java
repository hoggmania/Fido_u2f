package fr.neowave.listeners;

import fr.neowave.beans.Logger;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@WebListener
public class RequestListener implements ServletRequestAttributeListener, ServletRequestListener {

    private Logger logger = new Logger();


    @Override
    public void attributeAdded(ServletRequestAttributeEvent servletRequestAttributeEvent) {


        HttpServletRequest request = (HttpServletRequest) servletRequestAttributeEvent.getServletRequest();
        this.logger.setServerSessionId(String.valueOf(request.getSession().getId()));



        this.logger.setUsername(String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("username")) == null ?
                "" : String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("username")));

        this.logger.setError(String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("errors")) == null ?
                "" : String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("errors")));

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        this.logger.setDateTimeStart(dateFormat.format(date));


        this.logger.setServerChallenge(
                String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("challengeRequest")) == null ?
                        String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("authenticateRequest")) == null ?
                                ""
                                : String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("authenticateRequest"))
                        : String.valueOf(servletRequestAttributeEvent.getServletRequest().getAttribute("challengeRequest")));

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

        try {
            InetAddress inetAddress = InetAddress.getByName(ip);

            logger.setIp(ip);
            logger.setReverseName(inetAddress.getHostName());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        logger.setUserAgent(((HttpServletRequest) servletRequestAttributeEvent.getServletRequest()).getHeader("user-agent"));


        try {
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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

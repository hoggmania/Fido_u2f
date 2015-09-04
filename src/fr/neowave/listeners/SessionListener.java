package fr.neowave.listeners;

import fr.neowave.beans.Logger;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

@WebListener
public class SessionListener implements HttpSessionAttributeListener, HttpSessionListener {


    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
        Logger logger = new Logger();
        StringBuilder attributes = new StringBuilder();

        attributes.append("{");

        Enumeration<String> enumeration = httpSessionBindingEvent.getSession().getAttributeNames();
        while (enumeration.hasMoreElements()){

            String temp = enumeration.nextElement();
            attributes.append(temp);
            attributes.append(":");
            attributes.append(httpSessionBindingEvent.getSession().getAttribute(temp));
            attributes.append(",");
        }
        if(attributes.length() > 2 ) attributes.deleteCharAt(attributes.length()-1);
        attributes.append("}");

        logger.setServerSessionId(httpSessionBindingEvent.getSession().getId());
        logger.setMessage("Ajout attribut session");
        logger.setContext(httpSessionBindingEvent.getSession().getServletContext().getContextPath());
        logger.setUsername(httpSessionBindingEvent.getSession().getAttribute("username") == null ? "User not logged" : String.valueOf(httpSessionBindingEvent.getSession().getAttribute("username")));
        logger.setSessionAttributes(attributes.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        logger.setDateTimeStart(simpleDateFormat.format(new Date(httpSessionBindingEvent.getSession().getCreationTime())));

        try {
            DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        Logger logger = new Logger();
        StringBuilder attributes = new StringBuilder();
        attributes.append("{");
        while (httpSessionEvent.getSession().getAttributeNames().hasMoreElements()){
            String temp = httpSessionEvent.getSession().getAttributeNames().nextElement();
            httpSessionEvent.getSession().getAttributeNames().nextElement();
            attributes.append(temp);
            attributes.append(":");
            attributes.append(httpSessionEvent.getSession().getAttribute(temp));
            attributes.append(",");
        }
        if(attributes.length() > 2 ) attributes.deleteCharAt(attributes.length()-1);
        attributes.append("}");

        logger.setServerSessionId(httpSessionEvent.getSession().getId());
        logger.setMessage("Creation de session");
        logger.setContext(httpSessionEvent.getSession().getServletContext().getContextPath());
        logger.setUsername(httpSessionEvent.getSession().getAttribute("username") == null ? "User not logged" : String.valueOf(httpSessionEvent.getSession().getAttribute("username")));
        logger.setSessionAttributes(attributes.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        logger.setDateTimeStart(simpleDateFormat.format(new Date(httpSessionEvent.getSession().getCreationTime())));

        try {
            httpSessionEvent.getSession().setAttribute("tokenTimeout", DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions().getDelayToPutToken());
            DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        Logger logger = new Logger();
        StringBuilder attributes = new StringBuilder();

        Enumeration<String> enumeration = httpSessionEvent.getSession().getAttributeNames();
        attributes.append("{");
        while (enumeration.hasMoreElements()){
            String temp = enumeration.nextElement();
            attributes.append(temp);
            attributes.append(":");
            attributes.append( httpSessionEvent.getSession().getAttribute(temp));
            attributes.append(",");
        }
        if(attributes.length() > 2) attributes.deleteCharAt(attributes.length()-1);
        attributes.append("}");


        logger.setServerSessionId(httpSessionEvent.getSession().getId());
        logger.setMessage("Destruction de session");
        logger.setContext(httpSessionEvent.getSession().getServletContext().getContextPath());
        logger.setUsername(httpSessionEvent.getSession().getAttribute("username") == null ? "User not logged" : String.valueOf(httpSessionEvent.getSession().getAttribute("username")));
        logger.setSessionAttributes(attributes.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        logger.setDateTimeStart(simpleDateFormat.format(new Date(httpSessionEvent.getSession().getCreationTime())));
        logger.setDateTimeEnd(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        if((System.currentTimeMillis() - httpSessionEvent.getSession().getLastAccessedTime()) < (httpSessionEvent.getSession().getMaxInactiveInterval()*1000)){
            logger.setEndType("Manual logout");
        }
        else{
            logger.setEndType("Timeout");
        }

        try {
            DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}

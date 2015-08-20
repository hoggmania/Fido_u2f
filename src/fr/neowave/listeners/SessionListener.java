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
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

@WebListener
public class SessionListener implements HttpSessionAttributeListener, HttpSessionListener {

    private Logger logger = new Logger();

    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        logger.setServerSessionId(httpSessionEvent.getSession().getId());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        logger.setDateTimeStart(dateFormat.format(date));

       try {
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        logger.setServerSessionId(httpSessionEvent.getSession().getId());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        logger.setDateTimeEnd(dateFormat.format(date));
        logger.setEndType("Timeout");
        try {
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getLoggerDao().create(logger);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}

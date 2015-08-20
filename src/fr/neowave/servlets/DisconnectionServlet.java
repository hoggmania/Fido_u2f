package fr.neowave.servlets;

import fr.neowave.beans.Logger;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@WebServlet("/disconnection")
public class DisconnectionServlet extends HttpServlet {

    public DisconnectionServlet(){
        super();
    }

    private Logger logger = new Logger();

    public void doGet(HttpServletRequest request, HttpServletResponse response){
       try{

           if(request.getSession().getAttribute("username") != null)request.getSession().invalidate();
           logger.setServerSessionId(request.getSession().getId());
           DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
           java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
           logger.setDateTimeEnd(dateFormat.format(date));
           logger.setEndType("Manual logout");
           try {
               DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getLoggerDao().create(logger);
           } catch (SQLException | IOException e) {
               e.printStackTrace();
           }
           response.sendRedirect(request.getContextPath().concat("/authentication"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

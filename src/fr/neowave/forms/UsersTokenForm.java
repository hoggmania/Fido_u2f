package fr.neowave.forms;


import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class UsersTokenForm extends Form {

    public void showToken(HttpServletRequest request){
        try {
            this.setObject(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException | ParseException e) {
            this.setError("default", e.getMessage());
        }
    }

    public void deleteToken(HttpServletRequest request){

        Registration registration = new Registration();
        registration.setUsername(String.valueOf(request.getSession().getAttribute("username")));
        registration.setKeyHandle(request.getParameter("keyHandle"));
        try {

            Options options = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().getOptions();
            if(!options.getUsersRemoveLastToken() && DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))).size() < 2){
                this.setError("default", "You can't remove your last token, option 7) a) activated");
            }else {
                DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().delete(registration);
                this.setMessage("Token has been deleted");
            }
        } catch (SQLException | ClassNotFoundException | IOException | ParseException e) {
            this.setError("default", e.getMessage());
        }
    }
}

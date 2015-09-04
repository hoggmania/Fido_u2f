package fr.neowave.forms;


import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.messages.Messages;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Formulaire de gestion des clés d'un utilisateur
 */
public class UsersTokenForm extends Form {

    /**
     * Renvoie les clés enregistrées dans le formulaire de retour
     * @param request HttpServletRequest
     */
    public void showToken(HttpServletRequest request){
        try {
            this.setObject(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException | ParseException e) {
            this.setError("default", e.getMessage());
        }
    }

    /**
     * Supprime la clé passée en paramètre
     * @param request HttpServletRequest
     */
    public void deleteToken(HttpServletRequest request){

        Registration registration = new Registration();
        registration.setUsername(String.valueOf(request.getSession().getAttribute("username")));
        registration.setKeyHandle(request.getParameter("keyHandle"));
        try {

            Options options = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions();
            if(!options.getUsersRemoveLastToken() && DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))).size() < 2){
                this.setError("option", Messages.TOKEN_CANT_DELETE_LAST_TOKEN);
            }else {
                DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().delete(registration);
            }
            if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(String.valueOf(request.getSession().getAttribute("username"))).isEmpty()) {
                request.getSession().setAttribute("u2fAuthenticated", false);
                request.getSession().setAttribute("hasKey", false);
            }

        } catch (SQLException | ClassNotFoundException | IOException | ParseException e) {
            this.setError("default", e.getMessage());
        }
    }

}

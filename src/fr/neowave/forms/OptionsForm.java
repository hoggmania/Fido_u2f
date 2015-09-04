package fr.neowave.forms;

import fr.neowave.beans.Options;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.messages.Messages;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Formulaire de gestion des options de l'administrateur
 */
public class OptionsForm extends Form{

    /**
     * Change les options passées en paramètre
     * @param request HttpServletRequest
     */
    public void change(HttpServletRequest request){
        Options options = new Options();

        options.setOnlyNeowave(Boolean.valueOf(String.valueOf(request.getParameter("onlyNeowave"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("onlyNeowave"))));
        options.setAdminReplaceUsersTokens(Boolean.valueOf(String.valueOf(request.getParameter("adminReplaceUsersTokens"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("adminReplaceUsersTokens"))));
        options.setUserCreateAccount(Boolean.valueOf(String.valueOf(request.getParameter("userCreateAccount"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("userCreateAccount"))));
        options.setUsersRegisterTheirOwnFirstToken(Boolean.valueOf(String.valueOf(request.getParameter("usersRegisterTheirOwnFirstToken"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersRegisterTheirOwnFirstToken"))));
        options.setUsersAddNewTokens(Boolean.valueOf(String.valueOf(request.getParameter("usersAddNewTokens"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersAddNewTokens"))));
        options.setUsersRemoveLastToken(Boolean.valueOf(String.valueOf(request.getParameter("usersRemoveLastToken"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersRemoveLastToken"))));
        options.setUsersSeeDetails(Boolean.valueOf(String.valueOf(request.getParameter("usersSeeDetails"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersSeeDetails"))));

        if(request.getParameter("sessionInactiveExpirationTime") != null && Long.valueOf(request.getParameter("sessionInactiveExpirationTime")) >= 30
                && request.getParameter("requestValidityTime") != null && Long.valueOf(request.getParameter("requestValidityTime")) >= 15
                && request.getParameter("delayToPutToken") != null && Long.valueOf(request.getParameter("delayToPutToken")) >= 25) {


            options.setSessionInactiveExpirationTime(Long.valueOf(request.getParameter("sessionInactiveExpirationTime")));
            options.setRequestValidityTime(Long.valueOf(request.getParameter("requestValidityTime")));
            options.setDelayToPutToken(Long.valueOf(request.getParameter("delayToPutToken")));
            try{
                DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().updateOptions(options);
                this.setMessage(Messages.OPTIONS_SUCCESS);
            } catch (SQLException e) {
                this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
            }
        }
        else{
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.OPTIONS_ERROR);
        }
    }

    /**
     * Stock les options dans le formulaire de retour
     */
    public void get(){
        try{
            this.setObject(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().getOptions());
        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Reset les options
     */
    public void resetOptions(){
        try {
            DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getOptionsDao().rollback();
        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Supprime les utilisateurs sauf l'admin, les clés et reset les options
     */
    public void deleteAll(){
        try {

            for(Registration registration : DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list()) DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().delete(registration);

            for(User user :  DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().list()) if(!user.getUsername().equals("admin")) DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().delete(user);

            resetOptions();
        } catch (SQLException | ClassNotFoundException | ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }
}

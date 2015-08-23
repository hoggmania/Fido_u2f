package fr.neowave.forms;

import fr.neowave.beans.Options;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class OptionsForm extends Form{

    public void change(HttpServletRequest request){
        Options options = new Options();

        options.setOnlyNeowave(Boolean.valueOf(String.valueOf(request.getParameter("onlyNeowave"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("onlyNeowave"))));
        options.setAdminReplaceUsersTokens(Boolean.valueOf(String.valueOf(request.getParameter("adminReplaceUsersTokens"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("adminReplaceUsersTokens"))));
        options.setUserCreateAccount(Boolean.valueOf(String.valueOf(request.getParameter("userCreateAccount"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("userCreateAccount"))));
        options.setUsersRegisterTheirOwnFirstToken(Boolean.valueOf(String.valueOf(request.getParameter("usersRegisterTheirOwnFirstToken"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersRegisterTheirOwnFirstToken"))));
        options.setUsersAddNewTokens(Boolean.valueOf(String.valueOf(request.getParameter("usersAddNewTokens"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersAddNewTokens"))));
        options.setUsersRemoveLastToken(Boolean.valueOf(String.valueOf(request.getParameter("usersRemoveLastToken"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersRemoveLastToken"))));
        options.setUsersSeeDetails(Boolean.valueOf(String.valueOf(request.getParameter("usersSeeDetails"))) != null && Boolean.valueOf(String.valueOf(request.getParameter("usersSeeDetails"))));

        try{
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().updateOptions(options);
            this.setMessage("Options have been changed");
        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void rollback(){
        try{
            DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().rollback();
            this.setMessage("Options have been rollback");
        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void get(){
        try{
            this.setObject(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getOptionsDao().getOptions());
        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void rollbackAll(){

        rollback();

    }
}

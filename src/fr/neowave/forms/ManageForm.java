package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;
import fr.neowave.messages.Messages;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire de gestion d'utilisateur pour l'admin
 */
public class ManageForm extends Form{

    private User user;

    /**
     * Stock les utilisateurs dans le formulaire de retour
     * @param request HttpServletRequest
     */
    public void showUsers(HttpServletRequest request){

        try {
            List<User> users = new ArrayList<>();
            if ((Boolean) request.getSession().getAttribute("hasKey")) users = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().list();
            else users.add(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser("admin"));

            for (User user : users){
                user.setRegistrations(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(user.getUsername()));
            }
            this.setUsers(users);

        } catch (SQLException | ClassNotFoundException | IOException | ParseException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Supprime l'utilisateur passé en paramètre
     * @param request HttpServletRequest
     */
    public void deleteUser(HttpServletRequest request){

        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin")){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_CANT_DELETE_YOURSELF);
                }else{
                    if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(user.getUsername()) != null ){

                        DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().delete(user);
                        this.setMessage("User has been deleted");
                    }
                }


            }


        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Change le mot de passe de l'utilisateur passé en paramètre
     * @param request HttpServletRequest
     */
    public void changePassword(HttpServletRequest request){

        try{
            if(validUsername(request.getParameter("username")) && validPassword(request.getParameter("password"))){
                if ((Boolean) request.getSession().getAttribute("hasKey")) {
                    DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().updatePassword(user);
                    this.setMessage("User password has been changed");
                }

            }

        } catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Suspend l'utilisateur passé en paramètre
     * @param request HttpServletRequest
     */
    public void suspendUser(HttpServletRequest request){
        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin")){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_CANT_SUSPEND_YOURSELF);
                }else{
                    user.setSuspended(!user.getSuspended());
                    if(user.getSuspended()) this.setMessage("User has been suspended"); else this.setMessage("User is no longer suspended");
                    DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().updateSuspended(user);
                }

            }



        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Supprime la clé passée en paramètre d'un utilisateur
     * @param request HttpServletRequest
     */
    public void deleteUsersToken(HttpServletRequest request){
        try{
            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin") && DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list("admin").size() < 2){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_CANT_DELETE_LAST_TOKEN);
                }
                else {
                    Registration toDelete = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().getRegistration(user.getUsername(), String.valueOf(request.getParameter("keyHandle")));
                    if(toDelete != null){
                        DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().delete(toDelete);
                        this.setMessage("Token has been suspended");
                    }
                }

                if(DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list(user.getUsername()).isEmpty()) {
                    if(!request.getSession().getAttribute("username").equals("admin")){
                        request.getSession().setAttribute("u2fAuthenticated", false);
                        request.getSession().setAttribute("hasKey", false);
                    }

                }

            }

        } catch (SQLException | ClassNotFoundException | ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    /**
     * Suspend une clé passée en paramètre d'un utilisateur
     * @param request
     */
    public void suspendUsersToken(HttpServletRequest request){
        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin") && DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().list("admin").size() < 2){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_CANT_SUSPEND_LAST_TOKEN);
                }
                else {
                    Registration toSuspend = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().getRegistration(user.getUsername(), String.valueOf(request.getParameter("keyHandle")));
                    toSuspend.setSuspended(!toSuspend.getSuspended());
                    if(toSuspend.getSuspended()) this.setMessage("Token has been suspended"); else this.setMessage("Token is no longer suspended");
                    DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getRegistrationDao().updateSuspended(toSuspend);
                }
            }



        } catch (SQLException | ClassNotFoundException | ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }


    /**
     * Vérifie que le nom d'utilisateur est valide
     * @param username String
     * @return Boolean
     * @throws SQLException
     */
    private Boolean validUsername(String username) throws SQLException {

        username = username.trim();


        if (!username.matches("[a-zA-Z0-9_@.]*")) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_USERNAME_WRONG_CHARACTERS);
            return false;
        } else if (username.length() > 32 || username.length() < 4) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_USERNAME_WRONG_LENGTH);
            return false;
        } else if (DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(username) == null) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_USER_NOT_FOUND);
            return false;
        }
        else {

            this.user = DaoFactory.getFactory(FactoryType.DEFAULT_FACTORY).getUserDao().getUser(username);
            return this.user != null;
        }

    }

    /**
     * Vérifie que le mot de passe est valide
     * @param password String
     * @return Boolean
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private Boolean validPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {



        password = password.trim();


        if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_PASSWORD_WRONG_CHARACTERS);
            return false;
        } else if (password.length() > 32 || password.length() < 4) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), Messages.MANAGE_PASSWORD_WRONG_CHARACTERS);
            return false;
        } else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            String pass = Base64.encode(md5.digest());

            this.user.setPassword(pass);
            return true;

        }
    }
}

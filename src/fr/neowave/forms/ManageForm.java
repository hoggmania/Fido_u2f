package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.FormErrors;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ManageForm extends Form{

    private User user;
    public void showUsers(HttpServletRequest request){

        try {
            List<User> users = new ArrayList<>();
            if ((Boolean) request.getSession().getAttribute("hasKey")) users = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().list();
            else users.add(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser("admin"));

            for (User user : users){
                user.setRegistrations(DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list(user.getUsername()));
            }
            this.setMessage("");
            this.setUsers(users);

        } catch (SQLException | ClassNotFoundException | IOException | ParseException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }


    }

    public void deleteUser(HttpServletRequest request){

        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin")){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), "You cannot delete yourself");
                }else{
                    DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().delete(user);
                }


            }


        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }
    public void changePassword(HttpServletRequest request){

        try{
            if(validUsername(request.getParameter("username")) && validPassword(request.getParameter("password"))){
                if ((Boolean) request.getSession().getAttribute("hasKey")) {
                    DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().updatePassword(user);
                }
                else this.setError(FormErrors.DEFAULT_ERR.toString(), "You have to register at least one token to change users password");

            }

        } catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }
    public void suspendUser(HttpServletRequest request){
        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin")){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), "You cannot suspend yourself");
                }else{
                    user.setSuspended(!user.getSuspended());
                    DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().updateSuspended(user);
                }

            }



        } catch (SQLException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }

    public void deleteUsersToken(HttpServletRequest request){
        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin") && DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list("admin").size() < 2){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), "You cannot delete your last token");
                }
                else {
                    Registration toDelete = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().getRegistration(user.getUsername(), String.valueOf(request.getParameter("keyHandle")));
                    DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().delete(toDelete);
                }

            }

        } catch (SQLException | ClassNotFoundException | ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }
    public void suspendUsersToken(HttpServletRequest request){
        try{

            if(validUsername(request.getParameter("username"))) {
                if(user.getUsername().equals("admin") && DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().list("admin").size() < 2){
                    this.setError(FormErrors.DEFAULT_ERR.toString(), "You cannot suspend your last token");
                }
                else {
                    Registration toSuspend = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().getRegistration(user.getUsername(), String.valueOf(request.getParameter("keyHandle")));
                    toSuspend.setSuspended(!toSuspend.getSuspended());
                    DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getRegistrationDao().updateSuspended(toSuspend);
                }
            }



        } catch (SQLException | ClassNotFoundException | ParseException | IOException e) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), e.getMessage());
        }
    }


    private Boolean validUsername(String username) throws SQLException {

        username = username.trim();


        if (!username.matches("[a-zA-Z0-9_@.]*")) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), "Username not found");
            return false;
        } else if (username.length() > 32 || username.length() < 4) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), "Username not found");
            return false;
        } else if (DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) == null) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), "User not found.");
            return false;
        }
        else {

            this.user = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username);

            return true;
        }

    }

    private Boolean validPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {



        password = password.trim();


        if (!password.matches("[a-zA-Z0-9]*")) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), "Wrong password");
            return false;
        } else if (password.length() > 32 || password.length() < 4) {
            this.setError(FormErrors.DEFAULT_ERR.toString(), "Wrong password");
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

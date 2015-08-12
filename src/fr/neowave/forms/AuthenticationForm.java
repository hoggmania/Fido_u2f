package fr.neowave.forms;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import fr.neowave.beans.Registration;
import fr.neowave.beans.User;
import fr.neowave.dao.factories.DaoFactory;
import fr.neowave.dao.factories.FactoryType;
import fr.neowave.forms.Exceptions.PasswordException;
import fr.neowave.forms.Exceptions.UsernameException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class AuthenticationForm {

    User user = null;

    public String authenticateWithoutKey(HttpServletRequest request){

        return "";
    }

    public String authenticateWithKey(HttpServletRequest request){

        return "";
    }


    private Boolean areValidParameters(HttpServletRequest request) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        username = username.trim();
        password = password.trim();

        if (!username.matches("[a-zA-Z0-9_@.]*"))
            throw new UsernameException("Username not found");
        else if (username.length() > 32 || username.length() < 6)
            throw new UsernameException("Username not found");
        else if (!password.matches("[a-zA-Z0-9]*")) throw new PasswordException("Wrong password");
        else if (password.length() > 32 || password.length() < 6)
            throw new PasswordException("Wrong password");
        else if (DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username) == null)
            throw new UsernameException("User not found.");
        else {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));

            this.user = DaoFactory.getFactory(FactoryType.MYSQL_FACTORY).getUserDao().getUser(username);

            if(!this.user.getPassword().equals(Base64.encode(md5.digest()))){
                throw new PasswordException("Wrong password");
            }
            else{
                return true;
            }
        }
    }

    public List<Registration> getUserKeys(HttpServletRequest request){
        return null;
    }
}

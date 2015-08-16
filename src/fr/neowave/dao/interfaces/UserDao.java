package fr.neowave.dao.interfaces;

import fr.neowave.beans.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao{

    void create(User user) throws SQLException;

    void updatePassword(User user) throws SQLException;

    void updateSuspended(User user) throws SQLException;

    void delete(User user) throws SQLException;

    List<User> list() throws SQLException;

    User getUser(String username) throws SQLException;

}

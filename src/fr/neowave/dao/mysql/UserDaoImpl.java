package fr.neowave.dao.mysql;

import fr.neowave.beans.User;
import fr.neowave.dao.interfaces.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The MySql implementation of userDao.
 * Used to perform actions on the MySql users table
 */
public class UserDaoImpl implements UserDao {

    /**
     * The database connection var
     */
    private Connection connection;

    /**
     * Give the connection to use
     * @param connection the database connection
     */
    public UserDaoImpl( Connection connection ) {
        this.connection = connection;
    }

    /**
     * Insert an user into the mysql database
     * @param user the user you want to insert
     * @throws SQLException
     */
    @Override
    public void create(User user) throws SQLException{
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, suspended) " +
                    "VALUES (?,?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setBoolean(3, user.getSuspended());


            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else{
                throw new SQLException("Insertion error");
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Change user's password
     * @param user the user to whom you want to change password
     * @throws SQLException
     */
    @Override
    public void updatePassword(User user) throws SQLException{
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ? ");
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getUsername());

            if (preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else{
                throw new SQLException("Update error");
            }


        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Change user's suspended attribute
     * @param user the user to whom you want to suspend or not
     * @throws SQLException
     */
    @Override
    public void updateSuspended(User user) throws SQLException{
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE users SET suspended = ? WHERE username = ? ");
            preparedStatement.setBoolean(1, user.getSuspended());
            preparedStatement.setString(2, user.getUsername());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
                throw new SQLException("Update error");
            }


        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Delete an user
     * @param user the user you want to delete
     * @throws SQLException
     */
    @Override
    public void delete(User user) throws SQLException{
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("DELETE FROM users WHERE username = ? ");
            preparedStatement.setString(1, user.getUsername());
            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
                throw new SQLException("Deletion error");
            }


        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Return an ordered list of users stored into the MySql users table
     * @return List
     * @throws SQLException
     */
    @Override
    public List<User> list() throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> list = new ArrayList<>();
        User temp;

        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM users ORDER BY username ASC");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                temp = new User();
                temp.setUsername(resultSet.getString(2));
                temp.setPassword(resultSet.getString(3));
                temp.setSuspended(resultSet.getBoolean(4));

                list.add(temp);
            }




        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

        return list;
    }

    /**
     * Return the user's information
     * @param username the user to whom you want to have information
     * @return User
     * @throws SQLException
     */
    @Override
    public User getUser(String username) throws SQLException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                user = new User();
                user.setUsername(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                user.setSuspended(resultSet.getBoolean(4));
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch(SQLException ex) {
                    throw new SQLException(ex);
                }
                throw new SQLException(e);
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

        return user;
    }
}

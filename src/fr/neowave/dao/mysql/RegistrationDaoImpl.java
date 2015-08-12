package fr.neowave.dao.mysql;

import fr.neowave.beans.Registration;
import fr.neowave.dao.interfaces.RegistrationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDaoImpl implements RegistrationDao   {


    private Connection connection;

    public RegistrationDaoImpl( Connection connection ) {
        this.connection = connection;
    }


    @Override
    public void create(Registration registration) throws SQLException {

        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("INSERT INTO registrations (username, publicKey, certificate, counter, keyHandle, date, hostname, suspend) " +
                    "VALUES (?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, registration.getUsername());
            preparedStatement.setString(2, registration.getPublicKey());
            preparedStatement.setBlob(3, registration.getCertificate());
            preparedStatement.setInt(4, registration.getCounter());
            preparedStatement.setString(5, registration.getKeyHandle());
            preparedStatement.setDate(6, registration.getTimestamp());
            preparedStatement.setString(7, registration.getHostname());
            preparedStatement.setBoolean(8, registration.getSuspended());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
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

    @Override
    public void updateCounter(Registration registration) throws SQLException {

        PreparedStatement preparedStatement= null;

        try {

            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE registrations SET counter = ? WHERE username = ? AND keyHandle = ?");
            preparedStatement.setInt(1, registration.getCounter());
            preparedStatement.setString(2, registration.getUsername());
            preparedStatement.setString(3, registration.getKeyHandle());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
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

    @Override
    public void updateSuspended(Registration registration) throws SQLException {

        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE registrations SET suspended = ? WHERE username = ? AND keyHandle = ?");
            preparedStatement.setBoolean(1, registration.getSuspended());
            preparedStatement.setString(2, registration.getUsername());
            preparedStatement.setString(3, registration.getKeyHandle());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
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



    @Override
    public void delete(Registration registration) throws SQLException  {
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("DELETE FROM registrations WHERE username = ? AND keyHandle = ?");
            preparedStatement.setString(1, registration.getUsername());
            preparedStatement.setString(2, registration.getKeyHandle());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }
            else {
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

    @Override
    public List<Registration> list() throws SQLException  {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Registration> list = new ArrayList<>();
        Registration registration;

        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM registrations ORDER BY username ASC");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                registration = new Registration();
                registration.setId(resultSet.getInt(1));
                registration.setUsername(resultSet.getString(2));
                registration.setKeyHandle(resultSet.getString(3));
                registration.setPublicKey(resultSet.getString(4));
                registration.setCertificate(resultSet.getBlob(5));
                registration.setCounter(resultSet.getInt(6));
                registration.setHostname(resultSet.getString(7));
                registration.setTimestamp(resultSet.getDate(8));
                registration.setSuspended(resultSet.getBoolean(9));

                list.add(registration);
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

    @Override
    public List<Registration> list(String username) throws SQLException  {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Registration> list = new ArrayList<>();
        Registration registration;

        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM registrations WHERE username = ? ORDER BY keyHandle ASC");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                registration = new Registration();
                registration.setId(resultSet.getInt(1));
                registration.setUsername(resultSet.getString(2));
                registration.setKeyHandle(resultSet.getString(3));
                registration.setPublicKey(resultSet.getString(4));
                registration.setCertificate(resultSet.getBlob(5));
                registration.setCounter(resultSet.getInt(6));
                registration.setHostname(resultSet.getString(7));
                registration.setTimestamp(resultSet.getDate(8));
                registration.setSuspended(resultSet.getBoolean(9));

                list.add(registration);
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

    @Override
    public Registration getRegistration(String username, String keyHandle) throws SQLException  {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Registration registration = null;
        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM registrations WHERE username = ? AND keyHandle = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, keyHandle);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                registration = new Registration();
                registration.setId(resultSet.getInt(1));
                registration.setUsername(resultSet.getString(2));
                registration.setKeyHandle(resultSet.getString(3));
                registration.setPublicKey(resultSet.getString(4));
                registration.setCertificate(resultSet.getBlob(5));
                registration.setCounter(resultSet.getInt(6));
                registration.setHostname(resultSet.getString(7));
                registration.setTimestamp(resultSet.getDate(8));
                registration.setSuspended(resultSet.getBoolean(9));
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

        return registration;
    }
}

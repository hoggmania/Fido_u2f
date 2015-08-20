package fr.neowave.dao.mysql;

import fr.neowave.beans.Logger;
import fr.neowave.beans.Registration;
import fr.neowave.dao.interfaces.LoggerDao;
import sun.rmi.runtime.Log;

import java.io.*;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoggerDaoImpl implements LoggerDao {


    private Connection connection;

    public LoggerDaoImpl( Connection connection ) {
        this.connection = connection;
    }

    @Override
    public void create(Logger log) throws SQLException{
        PreparedStatement preparedStatement= null;

        connection.setAutoCommit(false);
        try {

            preparedStatement = connection.prepareStatement("INSERT INTO logs (serverSessionId, username, userAgent, " +
                    "ip, reverseName, dateTimeStart, dateTimeEnd, endType, serverChallenge, error) VALUES (?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, log.getServerSessionId());
            preparedStatement.setString(2, log.getUsername());
            preparedStatement.setString(3, log.getUserAgent());
            preparedStatement.setString(4, log.getIp());
            preparedStatement.setString(5, log.getReverseName());
            preparedStatement.setString(6, log.getDateTimeStart());
            preparedStatement.setString(7, log.getDateTimeEnd());
            preparedStatement.setString(8, log.getEndType());
            preparedStatement.setString(9, log.getServerChallenge());
            preparedStatement.setString(10, log.getError());
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
    public List<Logger> list() throws SQLException{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Logger> list = new ArrayList<>();
        Logger log;


        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM logs ORDER BY serverSessionId ASC");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                log = new Logger();
                log.setServerSessionId(resultSet.getString(1));
                log.setUsername(resultSet.getString(2));
                log.setUserAgent(resultSet.getString(3));
                log.setIp(resultSet.getString(4));
                log.setReverseName(resultSet.getString(5));
                log.setDateTimeStart(resultSet.getString(6));
                log.setDateTimeEnd(resultSet.getString(7));
                log.setEndType(resultSet.getString(8));
                log.setServerChallenge(resultSet.getString(9));
                log.setError(resultSet.getString(10));
                list.add(log);
            }


        } catch (SQLException e) {

            throw new SQLException(e);

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

        return list;
    }

    @Override
    public Logger getLog(String session) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Logger log;


        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM logs WHERE serverSessionId = ? AND id IN (SELECT MIN(id) FROM logs WHERE serverSessionId = ?)");
            preparedStatement.setString(1, session);
            preparedStatement.setString(2, session);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                log = new Logger();
                log.setServerSessionId(resultSet.getString(1));
                log.setUsername(resultSet.getString(2));
                log.setUserAgent(resultSet.getString(3));
                log.setIp(resultSet.getString(4));
                log.setReverseName(resultSet.getString(5));
                log.setDateTimeStart(resultSet.getString(6));
                log.setDateTimeEnd(resultSet.getString(7));
                log.setEndType(resultSet.getString(8));
                log.setServerChallenge(resultSet.getString(9));
                log.setError(resultSet.getString(10));
            }
            else{
                throw new SQLException("There is no log with this session ID");
            }
        } catch (SQLException e) {

            throw new SQLException(e);

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return log;
    }
}

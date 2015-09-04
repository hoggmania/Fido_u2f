package fr.neowave.dao.mysql;

import fr.neowave.beans.Logger;
import fr.neowave.dao.interfaces.LoggerDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

            preparedStatement = connection.prepareStatement("INSERT INTO logs (serverSessionId, message, context, username, browserName, browserVersion, " +
                    "osName, osVersion, ip, reverseName, requestParameters, requestAttributes, requestErrors, sessionAttributes," +
                    "dateTimeStart, dateTimeEnd, endType) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, log.getServerSessionId());
            preparedStatement.setString(2, log.getMessage());
            preparedStatement.setString(3, log.getContext());
            preparedStatement.setString(4, log.getUsername());
            preparedStatement.setString(5, log.getBrowserName());
            preparedStatement.setString(6, log.getBrowserVersion());
            preparedStatement.setString(7, log.getOsName());
            preparedStatement.setString(8, log.getOsVersion());
            preparedStatement.setString(9, log.getIp());
            preparedStatement.setString(10, log.getReverseName());
            preparedStatement.setString(11, log.getRequestParameters());
            preparedStatement.setString(12, log.getRequestAttributes());
            preparedStatement.setString(13, log.getRequestErrors());
            preparedStatement.setString(14, log.getSessionAttributes());
            preparedStatement.setString(15, log.getDateTimeStart());
            preparedStatement.setString(16, log.getDateTimeEnd());
            preparedStatement.setString(17, log.getEndType());

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

            preparedStatement = connection.prepareStatement("SELECT * FROM logs ORDER BY dateTimeStart ASC, serverSessionId ASC");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                log = new Logger();
                log.setServerSessionId(resultSet.getString(2));
                log.setMessage(resultSet.getString(3));
                log.setContext(resultSet.getString(4));
                log.setUsername(resultSet.getString(5));
                log.setBrowserName(resultSet.getString(6));
                log.setBrowserVersion(resultSet.getString(7));
                log.setOsName(resultSet.getString(8));
                log.setOsVersion(resultSet.getString(9));
                log.setIp(resultSet.getString(10));
                log.setReverseName(resultSet.getString(11));
                log.setRequestParameters(resultSet.getString(12));
                log.setRequestAttributes(resultSet.getString(13));
                log.setRequestErrors(resultSet.getString(14));
                log.setSessionAttributes(resultSet.getString(15));
                log.setDateTimeStart(resultSet.getString(16));
                log.setDateTimeEnd(resultSet.getString(17));
                log.setEndType(resultSet.getString(18));
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
    public List<Logger> listActivity() throws SQLException, ParseException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Logger> list = new ArrayList<>();
        Logger log;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis()-(5*60*1000));
        Date date;
        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM logs");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                date = simpleDateFormat.parse(resultSet.getString(16));

                if((date.after(currentDate))){
                    log = new Logger();
                    log.setServerSessionId(resultSet.getString(2));
                    log.setMessage(resultSet.getString(3));
                    log.setContext(resultSet.getString(4));
                    log.setUsername(resultSet.getString(5));
                    log.setBrowserName(resultSet.getString(6));
                    log.setBrowserVersion(resultSet.getString(7));
                    log.setOsName(resultSet.getString(8));
                    log.setOsVersion(resultSet.getString(9));
                    log.setIp(resultSet.getString(10));
                    log.setReverseName(resultSet.getString(11));
                    log.setRequestParameters(resultSet.getString(12));
                    log.setRequestAttributes(resultSet.getString(13));
                    log.setRequestErrors(resultSet.getString(14));
                    log.setSessionAttributes(resultSet.getString(15));
                    log.setDateTimeStart(resultSet.getString(16));
                    log.setDateTimeEnd(resultSet.getString(17));
                    log.setEndType(resultSet.getString(18));
                    list.add(log);
                }

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
    public void delete() throws SQLException{
        PreparedStatement preparedStatement= null;

        connection.setAutoCommit(false);
        try {

            preparedStatement = connection.prepareStatement("DELETE FROM logs");

            if(preparedStatement.executeUpdate() > 0){
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
}

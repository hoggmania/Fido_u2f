package fr.neowave.dao.mysql;

import fr.neowave.beans.Options;
import fr.neowave.dao.interfaces.OptionsDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OptionsDaoImpl implements OptionsDao {
    private Connection connection;

    public OptionsDaoImpl( Connection connection ) {
        this.connection = connection;
    }

    @Override
    public Options getOptions() throws SQLException {

        ResultSet resultSet;
        PreparedStatement preparedStatement= null;
        Options options = null;
        try {
            options = new Options();
            preparedStatement = connection.prepareStatement("SELECT * FROM options WHERE role = 'current'");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            options.setOnlyNeowave(Boolean.valueOf(resultSet.getString(2)));
            options.setAdminReplaceUsersTokens(Boolean.valueOf(resultSet.getString(3)));
            options.setUserCreateAccount(Boolean.valueOf(resultSet.getString(4)));
            options.setUsersRegisterTheirOwnFirstToken(Boolean.valueOf(resultSet.getString(5)));
            options.setUsersAddNewTokens(Boolean.valueOf(resultSet.getString(6)));
            options.setUsersRemoveLastToken(Boolean.valueOf(resultSet.getString(7)));
            options.setUsersSeeDetails(Boolean.valueOf(resultSet.getString(8)));
            options.setSessionInactiveExpirationTime(resultSet.getLong(9));
            options.setRequestValidityTime(resultSet.getLong(10));
            options.setDelayToPutToken(resultSet.getLong(11));
        } catch (SQLException e) {
            throw new SQLException(e);

        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

        return options;
    }

    @Override
    public void updateOptions(Options options) throws SQLException {
        PreparedStatement preparedStatement= null;

        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("UPDATE options SET onlyNeowave = ?, userCreateAccount = ?, userRegisterFirstToken = ?, " +
                    "userRemoveLastToken = ?, adminTokenReplaceUserToken = ?, userAddNewToken = ?, showDebug = ?, " +
                    "sessionInactiveExpirationTime = ?, requestValidityTime = ?, timeToPlugToken = ? WHERE role = 'current'");

            preparedStatement.setString(1, String.valueOf(options.getOnlyNeowave()));
            preparedStatement.setString(2, String.valueOf(options.getAdminReplaceUsersTokens()));
            preparedStatement.setString(3, String.valueOf(options.getUserCreateAccount()));
            preparedStatement.setString(4, String.valueOf(options.getUsersRegisterTheirOwnFirstToken()));
            preparedStatement.setString(5, String.valueOf(options.getUsersAddNewTokens()));
            preparedStatement.setString(6, String.valueOf(options.getUsersRemoveLastToken()));
            preparedStatement.setString(7, String.valueOf(options.getUsersSeeDetails()));
            preparedStatement.setLong(8, options.getSessionInactiveExpirationTime());
            preparedStatement.setLong(9, options.getRequestValidityTime());
            preparedStatement.setLong(10, options.getDelayToPutToken());
            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }else{
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

    @Override
    public void rollback() throws SQLException {
        ResultSet resultSet;
        PreparedStatement preparedStatement= null;
        Options options;
        try {
            connection.setAutoCommit(false);
            options = new Options();
            preparedStatement = connection.prepareStatement("SELECT * FROM options WHERE role = 'rollback'");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            options.setOnlyNeowave(Boolean.valueOf(resultSet.getString(2)));
            options.setAdminReplaceUsersTokens(Boolean.valueOf(resultSet.getString(3)));
            options.setUserCreateAccount(Boolean.valueOf(resultSet.getString(4)));
            options.setUsersRegisterTheirOwnFirstToken(Boolean.valueOf(resultSet.getString(5)));
            options.setUsersAddNewTokens(Boolean.valueOf(resultSet.getString(6)));
            options.setUsersRemoveLastToken(Boolean.valueOf(resultSet.getString(7)));
            options.setUsersSeeDetails(Boolean.valueOf(resultSet.getString(8)));
            options.setSessionInactiveExpirationTime(resultSet.getLong(9));
            options.setRequestValidityTime(resultSet.getLong(10));
            options.setDelayToPutToken(resultSet.getLong(11));

            preparedStatement = connection.prepareStatement("UPDATE options SET onlyNeowave = ?, userCreateAccount = ?, userRegisterFirstToken = ?, " +
                    "userRemoveLastToken = ?, adminTokenReplaceUserToken = ?, userAddNewToken = ?, showDebug = ?, " +
                    "sessionInactiveExpirationTime = ?, requestValidityTime = ?, timeToPlugToken = ? WHERE role = 'current'");

            preparedStatement.setString(1, String.valueOf(options.getOnlyNeowave()));
            preparedStatement.setString(2, String.valueOf(options.getAdminReplaceUsersTokens()));
            preparedStatement.setString(3, String.valueOf(options.getUserCreateAccount()));
            preparedStatement.setString(4, String.valueOf(options.getUsersRegisterTheirOwnFirstToken()));
            preparedStatement.setString(5, String.valueOf(options.getUsersAddNewTokens()));
            preparedStatement.setString(6, String.valueOf(options.getUsersRemoveLastToken()));
            preparedStatement.setString(7, String.valueOf(options.getUsersSeeDetails()));
            preparedStatement.setLong(8, options.getSessionInactiveExpirationTime());
            preparedStatement.setLong(9, options.getRequestValidityTime());
            preparedStatement.setLong(10, options.getDelayToPutToken());

            if(preparedStatement.executeUpdate() == 1){
                connection.commit();
            }else{
                throw new SQLException("Rollback error");
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

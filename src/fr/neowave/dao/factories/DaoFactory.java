package fr.neowave.dao.factories;

import fr.neowave.dao.interfaces.LoggerDao;
import fr.neowave.dao.interfaces.OptionsDao;
import fr.neowave.dao.interfaces.RegistrationDao;
import fr.neowave.dao.interfaces.UserDao;

import java.sql.SQLException;

public abstract class DaoFactory {


    public static DaoFactory getFactory(FactoryType type){
        if(type.equals(FactoryType.MYSQL_FACTORY)){
            return MySqlDaoFactory.getInstance();
        }
        return MySqlDaoFactory.getInstance();
    }


    public abstract UserDao getUserDao() throws SQLException;

    public abstract RegistrationDao getRegistrationDao() throws SQLException;

    public abstract LoggerDao getLoggerDao() throws SQLException;

    public abstract OptionsDao getOptionsDao() throws SQLException;
}
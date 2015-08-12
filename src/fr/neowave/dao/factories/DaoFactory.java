package fr.neowave.dao.factories;

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

    /*
     * Méthodes de récupération de l'implémentation des différents DAO (un seul
     * pour le moment)
     */
    public abstract UserDao getUserDao() throws SQLException;

    public abstract RegistrationDao getRegistrationDao() throws SQLException;
}
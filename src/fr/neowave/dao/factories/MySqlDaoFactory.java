package fr.neowave.dao.factories;

import fr.neowave.dao.factories.exceptions.DaoConfigurationException;
import fr.neowave.dao.interfaces.RegistrationDao;
import fr.neowave.dao.interfaces.UserDao;
import fr.neowave.dao.mysql.RegistrationDaoImpl;
import fr.neowave.dao.mysql.UserDaoImpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlDaoFactory extends DaoFactory{

    private static final String PROPERTIES_FILE      = "/fr/neowave/dao/mysql/mysql.properties";
    private static final String PROPERTY_URL             = "url";
    private static final String PROPERTY_DRIVER          = "driver";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD   = "password";

    private String              url;
    private String              username;
    private String              password;


    public MySqlDaoFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    /*
     * Méthode chargée de récupérer les informations de connexion à la base de
     * données, charger le driver JDBC et retourner une instance de la Factory
     */
    public static DaoFactory getInstance() throws DaoConfigurationException {
        Properties properties = new Properties();
        String url;
        String driver;
        String username;
        String password;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream PropertiesFile = classLoader.getResourceAsStream( PROPERTIES_FILE );

        if ( PropertiesFile == null ) {
            throw new DaoConfigurationException( "Properties file" + PROPERTIES_FILE + " not found." );
        }

        try {
            properties.load( PropertiesFile );
            url = properties.getProperty( PROPERTY_URL );
            driver = properties.getProperty( PROPERTY_DRIVER );
            username = properties.getProperty( PROPERTY_USERNAME );
            password = properties.getProperty( PROPERTY_PASSWORD );
        } catch ( IOException e ) {
            throw new DaoConfigurationException( "Can't load properties file " + PROPERTIES_FILE, e );
        }

        try {
            Class.forName( driver );
        } catch ( ClassNotFoundException e ) {
            throw new DaoConfigurationException( "Can't find driver in classpath", e );
        }

        return new MySqlDaoFactory( url, username, password );
    }

    /* Méthode chargée de fournir une connexion à la base de données */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /*
     * Méthodes de récupération de l'implémentation des différents DAO (un seul
     * pour le moment)
     */
    public UserDao getUserDao() throws SQLException {
        return new UserDaoImpl( this.getConnection() );
    }

    public RegistrationDao getRegistrationDao() throws SQLException {
        return new RegistrationDaoImpl(this.getConnection());
    }
}

package fr.neowave.dao.interfaces;

import fr.neowave.beans.Registration;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Elekhyr on 04/08/2015.
 */
public interface RegistrationDao{

    void create(Registration registration) throws SQLException;

    void updateCounter(Registration registration) throws SQLException;

    void updateSuspended(Registration registration) throws SQLException;

    void delete(Registration registration) throws SQLException ;

    List<Registration> list() throws SQLException ;
    List<Registration> list(String username) throws SQLException ;
    Registration getRegistration(String username, String keyHandle) throws SQLException ;
}

package fr.neowave.dao.interfaces;

import fr.neowave.beans.Registration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface RegistrationDao{

    void create(Registration registration) throws SQLException, IOException;

    void updateCounter(Registration registration) throws SQLException;

    void updateSuspended(Registration registration) throws SQLException;

    void delete(Registration registration) throws SQLException ;

    List<Registration> list() throws SQLException, ClassNotFoundException, IOException;
    List<Registration> list(String username) throws SQLException, IOException, ClassNotFoundException;
    Registration getRegistration(String username, String keyHandle) throws SQLException, ClassNotFoundException, IOException;
}

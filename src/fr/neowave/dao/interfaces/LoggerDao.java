package fr.neowave.dao.interfaces;

import fr.neowave.beans.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface LoggerDao {

    void create(Logger log) throws SQLException, IOException;

    List<Logger> list() throws SQLException, ClassNotFoundException, IOException;

    List<Logger> listActivity() throws SQLException, ParseException;

    void delete() throws SQLException;
}

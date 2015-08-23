package fr.neowave.dao.interfaces;

import fr.neowave.beans.Options;

import java.sql.SQLException;

public interface OptionsDao {

    Options getOptions() throws SQLException;
    void updateOptions(Options options) throws SQLException;
    void rollback() throws SQLException;
}

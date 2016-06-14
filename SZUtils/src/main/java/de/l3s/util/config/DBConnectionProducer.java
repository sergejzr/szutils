package de.l3s.util.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnectionProducer {

	public Connection getConnection() throws SQLException;

}

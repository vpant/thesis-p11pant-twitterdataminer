package org.twittercity.twitterdataminer.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

public class DatabaseManager {
	
	private final String DB_PORT = "3306";
	private final String DB_NAME = "twittercity";
	private final String DB_USERNAME = "root";
	private final String DB_PASSWORD = "";
	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost:" + DB_PORT + "/" + DB_NAME;
	
	private  DataSource dataSource = null;
	
	private static DatabaseManager instance = null;
	
	private DatabaseManager(){}
	
	// If database we are getting a fatal error if database exists but not tables error message no table found.
	// Initial setup we should create the database schema. See if jenkins can help you maintain test database
	private void setupDataSource() throws SQLException {	
		//Turning off c3p0's logging. If connection errors occurred remove these lines.
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		try {
			Class.forName(DRIVER_CLASS_NAME);
			DataSource unpooled = DataSources.unpooledDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);
			Map<String, Object> overrideProps = new HashMap<String, Object>();

			overrideProps.put("maxPoolSize", 5);
			overrideProps.put("minPoolSize", 2);

			dataSource = DataSources.pooledDataSource(unpooled, overrideProps);
		} catch (ClassNotFoundException cnfe) {
			throw new SQLException("MySQL Driver could not be loaded: ", cnfe);
		}
	}
	
	// Singleton
	public Connection getConnection () throws SQLException {
		if (dataSource == null) {
			setupDataSource();
		}
		
		return dataSource.getConnection();		
	}
	
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}
	
	public String getDBPort() {
		return DB_PORT;
	}
	
	public String getDBName() {
		return DB_NAME;
	}
	
	public String getDBUsername() {
		return DB_USERNAME;
	}
	
	public String getDBPassword() {
		return DB_PASSWORD;
	}
	
	public String getDBURL() {
		return DB_URL;
	}
	
	public String getDBDriver() {
		return DRIVER_CLASS_NAME;
	}	
}

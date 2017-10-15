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
	
	private void setupDataSource() throws SQLException
	{	
		//Turning off c3p0's logging. If connection errors occured remove these lines.
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
	
	
	public Connection getConnection () throws SQLException
	{
		if (dataSource == null)
		{
			setupDataSource();
		}
		
		return dataSource.getConnection();
			
	}
	
	public static DatabaseManager getInstance()
	{
		if (instance == null)
		{
			instance = new DatabaseManager();
		}
		return instance;
	}
}

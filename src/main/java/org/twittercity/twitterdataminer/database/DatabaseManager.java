package org.twittercity.twitterdataminer.database;

import java.io.File;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
	
	private static DatabaseManager instance = null;
	private DatabaseManager(){}

	// Singleton
	public Session getConnection () throws SQLException {
		//Check if file found
		File hibernateConfig = new File(DatabaseManager.class.getClassLoader().getResource("hibernate.cfg.xml").getFile());
		SessionFactory sf =  new Configuration().configure(hibernateConfig).buildSessionFactory();
		Session session = sf.openSession();
		return session;		
	}
	
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}
}

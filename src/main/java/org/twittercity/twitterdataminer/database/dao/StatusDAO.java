package org.twittercity.twitterdataminer.database.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.database.DatabaseManager;
import org.twittercity.twitterdataminer.twitter.models.Status;

public class StatusDAO {
	
	private StatusDAO() {}
	
	private static Logger logger = LoggerFactory.getLogger(StatusDAO.class);
	
	public static void saveTweets(List<Status> tweets) {
		
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			
			for (Status tweet : tweets) {
			
				session.beginTransaction();
				session.save(tweet);
				session.getTransaction().commit();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			//logger.error(e.getMessage());
		}
	}
	
}

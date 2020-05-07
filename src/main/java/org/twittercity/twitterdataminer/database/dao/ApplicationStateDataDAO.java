package org.twittercity.twitterdataminer.database.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.database.DatabaseManager;
import org.twittercity.twitterdataminer.init.ApplicationStateData;

public class ApplicationStateDataDAO {
	
	private static Logger logger = LoggerFactory.getLogger(ApplicationStateDataDAO.class);
	
	private ApplicationStateDataDAO() {}
	
	public static void saveBearerToken(String bearerToken) {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {

			ApplicationStateData appState = session.get(ApplicationStateData.class, 0);
			if(appState == null) {
				appState = new ApplicationStateData();
			}
			appState.setBearerToken(bearerToken);
			saveAppStateObject(session, appState);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void saveLastSearchedQueryId(int queryId) {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {

			ApplicationStateData appState = session.get(ApplicationStateData.class, 0);
			if(appState == null) {
				appState = new ApplicationStateData();
			}
			appState.setLastSearchedQueryId(queryId);
			saveAppStateObject(session, appState);
		} catch (HibernateException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static String getBearerToken() {
		ApplicationStateData appState = getAppStateObject();
		return (appState != null) ? appState.getBearerToken() : "" ;
	}
	
	
	/**
	 * Retrieves from the database the last query id used to request Twitter Search API
	 * @return queryId The query id or 1 if it does not exists
	 */
	public static int getLastSearchedQueryId() {
		ApplicationStateData appState = getAppStateObject();
		logger.debug("getLastSearchedQueryId is: {}", appState.getLastSearchedQueryId());
		
		return appState.getLastSearchedQueryId();
	}
	
	private static ApplicationStateData getAppStateObject() {
		ApplicationStateData appState = null;
		
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			appState = session.get(ApplicationStateData.class, 0);
			if(appState == null) {
				appState = new ApplicationStateData();
			}
			saveAppStateObject(session, appState);
		}
		
		return appState;
	}
	
	private static void saveAppStateObject(Session session, ApplicationStateData appState) {		
		session.beginTransaction();
		session.save(appState);
		session.getTransaction().commit();
	}
}

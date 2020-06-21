package org.twittercity.twitterdataminer.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.twittercity.twitterdataminer.database.DatabaseManager;
import org.twittercity.twitterdataminer.twitter.models.State;

public class StateDAO {

	private StateDAO() {}
	
	public static State getStateById(int stateId) {
		State state = null;
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			state = session.get(State.class, stateId);
		}
		return state;
	}
	
	public static boolean isUSAStateTableEmpty() {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			return session.createQuery("SELECT 1 FROM State").setMaxResults(1).list().isEmpty();
		}
	}
	

	public static void saveOrUpdate(State state) {
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(state);
			tx.commit();
		}
	}
	
}

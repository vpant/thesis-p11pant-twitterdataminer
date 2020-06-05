package org.twittercity.twitterdataminer.database.dao;

import org.hibernate.Session;
import org.twittercity.twitterdataminer.database.DatabaseManager;
import org.twittercity.twitterdataminer.twitter.models.State;

public class StateDAO {

	
	public static State getStateById(int stateId) {
		State state = null;
		try ( Session session = DatabaseManager.getSessionFactory().openSession();) {
			state = session.get(State.class, stateId);
		}
		return state;
	}
	
}

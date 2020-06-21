package org.twittercity.twitterdataminer.init;

import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.database.dao.StateDAO;
import org.twittercity.twitterdataminer.twitter.models.State;
import org.twittercity.twitterdataminer.twitter.models.USAState;

public class DBMigration {
	
	private DBMigration() {}
	
	public static void buildTables() throws TwitterException {
		
		Keywords.buildTable();
		usaStatesBuildTable();
	}

	private static void usaStatesBuildTable() {
		if(!StateDAO.isUSAStateTableEmpty()) {
			return;
		}
		
		for (USAState state: USAState.values()) {
			StateDAO.saveOrUpdate(new State(state));
        }
	}
	
}

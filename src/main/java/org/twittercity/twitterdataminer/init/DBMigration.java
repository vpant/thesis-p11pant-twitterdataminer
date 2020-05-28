package org.twittercity.twitterdataminer.init;

import org.twittercity.twitterdataminer.TwitterException;

public class DBMigration {
	
	private DBMigration() {}
	
	public static void buildTables() throws TwitterException {
		
		Keywords.buildTable();
		
	}
	
}

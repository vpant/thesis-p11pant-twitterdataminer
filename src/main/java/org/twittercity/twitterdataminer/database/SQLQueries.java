package org.twittercity.twitterdataminer.database;

public class SQLQueries {

	public static final String SAVE_TWEETS = "INSERT INTO tweets (author, author_account_id, id_str, text, date) VALUES (?, ?, ?, ?, ?)";
	public static final String GET_ALL_TWEETS = "SELECT * FROM tweets";

}

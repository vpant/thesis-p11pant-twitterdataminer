package org.twittercity.twitterdataminer.database;

public class SQLQueries {

	public static final String CREATE_DATABASE_SCHEMA = "CREATE DATABASE IF NOT EXISTS " + DatabaseManager.getInstance().getDBName();
	
	public static final String SAVE_TWEETS = "INSERT INTO tweets (author, author_account_id, id_str, text, date, profile_pic_url) VALUES (?, ?, ?, ?, ?, ?)";
	public static final String GET_ALL_TWEETS = "SELECT * FROM tweets";

}

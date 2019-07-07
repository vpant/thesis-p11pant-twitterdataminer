package org.twittercity.twitterdataminer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.searchtwitter.Status;

public class StatusDAO implements IStatusDAO {
	
	public void saveTweets(List<Status> tweets) throws TwitterException {
		try ( Connection conn = DatabaseManager.getInstance().getConnection();
			  PreparedStatement statement = conn.prepareStatement(SQLQueries.SAVE_TWEETS); ) {
			int i = 0;
			
			for (Status tweet : tweets) {
				statement.setString(1, tweet.getTwitterAccountName());
				statement.setString(2, tweet.getTwitterAccountID());
				statement.setString(3, tweet.getId());
				statement.setString(4, tweet.getText());
				statement.setString(5, tweet.getCreatedAt());
				
				statement.addBatch();
				i++;

				if ((i % 1000 == 0) || (i == tweets.size())) {
					statement.executeBatch(); // Execute every 1000 tweets.
				}
			}
		}catch (SQLException sqle) {
			throw new TwitterException("An SQLExcpetion was happened while saving the tweets. The message is: " + sqle.getMessage());
		}
	}
	
	public List<Status> getAllTweets() throws TwitterException {
		List<Status> tweets = new ArrayList<Status>();
		
		try ( Connection conn = DatabaseManager.getInstance().getConnection();
				  Statement statement = conn.createStatement(); ) {
			ResultSet rs = statement.executeQuery(SQLQueries.GET_ALL_TWEETS);
			while(rs.next()) {
				String date = rs.getString("date");
				String text = rs.getString("text");
				String author = rs.getString("author");
				String authorAccountID = rs.getString("author_account_id") ;
				String idStr = rs.getString("id_str");
				
				tweets.add(new Status(idStr, date, text, author, authorAccountID));
			}
		} catch (SQLException sqle) {
			throw new TwitterException("An SQLExcpetion was happened while I was saving the tweets. The message is: " + sqle.getMessage());
		}
		
		return tweets;
	}
}

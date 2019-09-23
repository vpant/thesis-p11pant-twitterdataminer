package org.twittercity.twitterdataminer.database;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterDataMiner;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.searchtwitter.Status;

public class StatusDAO {
	
	Logger logger = LoggerFactory.getLogger(StatusDAO.class);
	
	public void saveTweets(List<Status> tweets) throws TwitterException {
		try ( Session session = DatabaseManager.getInstance().getConnection();) {
			
			for (Status tweet : tweets) {
				session.beginTransaction();
				session.save(tweet);
				session.getTransaction().commit();
			}
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Status> getAllTweets() throws TwitterException {
		/*List<Status> tweets = new ArrayList<Status>();
		 try {
			Session session = DatabaseManager.getInstance().getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				  Statement statement = conn.createStatement(); ) {
			ResultSet rs = statement.executeQuery(SQLQueries.GET_ALL_TWEETS);
			while(rs.next()) {
				String date = rs.getString("date");
				String text = rs.getString("text");
				String author = rs.getString("author");
				String authorAccountID = rs.getString("author_account_id") ;
				String idStr = rs.getString("id_str");
				String profilePicUrl = rs.getString("");
				int feeling = rs.getInt("feeling");
				tweets.add(new Status(idStr, date, text, author, authorAccountID, profilePicUrl, feeling));
			}
		} catch (SQLException sqle) {
			throw new TwitterException("An SQLExcpetion was happened while I was saving the tweets. The message is: " + sqle.getMessage());
		}
		
		return tweets;*/
		return null;
	}
}

package org.twittercity.twitterdataminer.database;

import java.util.List;

import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.searchtwitter.Status;

public interface IStatusDAO {

	public void saveTweets(List<Status> tweets) throws TwitterException;
	public List<Status> getAllTweets() throws TwitterException;
}

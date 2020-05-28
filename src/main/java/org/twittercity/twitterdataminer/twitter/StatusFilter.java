package org.twittercity.twitterdataminer.twitter;

import java.util.List;

import org.twittercity.twitterdataminer.twitter.models.Status;

/**
 * Class with static methods to exclude unnecessary tweets for the app's purpose
 *
 */
public class StatusFilter {

	private StatusFilter() {}
	
	public static List<Status> filterTweets(List<Status> tweets) {
		
		for(Status tweet : tweets) {
			
			if(tweet.getState() == null) {
				tweets.remove(tweet);
			}
		}
		
		return tweets;
	}
	
}

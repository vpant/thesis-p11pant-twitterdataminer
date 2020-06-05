package org.twittercity.twitterdataminer.twitter;

import java.util.Iterator;
import java.util.List;

import org.twittercity.twitterdataminer.twitter.models.Status;

/**
 * Class with static methods to exclude unnecessary tweets for the app's purpose
 *
 */
public class StatusFilter {

	private StatusFilter() {}
	
	public static void filterTweets(List<Status> tweets) {
		
		for(Iterator<Status> statusIt = tweets.iterator(); statusIt.hasNext();) {
			Status tweet = statusIt.next();
			if(tweet.getState() == null) {
				statusIt.remove();
			}
		}

	}
	
}

package org.twittercity.twitterdataminer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.database.IStatusDAO;
import org.twittercity.twitterdataminer.database.StatusDAO;
import org.twittercity.twitterdataminer.searchtwitter.TwitterSearch;


public class TwitterDataMiner {

    public static void main(String[] args) 
    {
		Logger logger = LoggerFactory.getLogger(TwitterDataMiner.class);

		IStatusDAO statusDao = new StatusDAO();
		try {
			statusDao.saveTweets(new TwitterSearch().search());
		} catch (TwitterException e) {
			logger.error(e.getMessage());
		}
		logger.info("Programm Finished!");
    }
    
}

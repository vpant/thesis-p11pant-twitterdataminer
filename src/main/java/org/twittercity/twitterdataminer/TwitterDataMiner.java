package org.twittercity.twitterdataminer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.database.StatusDAO;
import org.twittercity.twitterdataminer.searchtwitter.TwitterSearch;


public class TwitterDataMiner {

    public static void main(String[] args) 
    {
		Logger logger = LoggerFactory.getLogger(TwitterDataMiner.class);

		StatusDAO statusDao = new StatusDAO();
		
		try {
			statusDao.saveTweets(new TwitterSearch().search());
		} /*catch (TwitterException e) {
			logger.error(e.getMessage());
		} */catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Programm Finished!");
    }
    
}

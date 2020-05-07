package org.twittercity.twitterdataminer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.init.Keywords;
import org.twittercity.twitterdataminer.twitter.Twitter;


public class TwitterDataMiner {

    public static void main(String[] args) 
    {
    	Logger logger = LoggerFactory.getLogger(TwitterDataMiner.class);
    	try {
			Keywords.buildTable();
		} catch (TwitterException e) {
			logger.error(e.getMessage());
			return;
		} 
    	
		Twitter.searchAndSave();
		
		logger.info("Program Finished!");
    }
    
}

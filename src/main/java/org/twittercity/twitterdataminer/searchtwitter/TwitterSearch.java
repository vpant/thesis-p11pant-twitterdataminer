package org.twittercity.twitterdataminer.searchtwitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.http.HttpRequest;
import org.twittercity.twitterdataminer.http.HttpResponse;
import org.twittercity.twitterdataminer.http.HttpResponseCode;
import org.twittercity.twitterdataminer.oauth2.OAuth2;
import org.twittercity.twitterdataminer.oauth2.OAuth2ConfigManager;

public class TwitterSearch {
	private Logger logger = LoggerFactory.getLogger(TwitterSearch.class);
	
	private final String API_URL = "https://api.twitter.com/1.1/search/tweets.json";
	private final int MAX_REQUESTS = 1; // 5
	private String apiQuery ="?q=build%20house%20-filter%3Alinks%20-filter%3Areplies%20lang%3Aen%20-filter%3Aretweets&result_type=recent&include_entities=1";
	private String cachedUrl; //Take it from the database!
	
	
	public TwitterSearch() throws TwitterException {
		
		String cachedUrl = OAuth2ConfigManager.getInstance().getCachedUrl();
		if(cachedUrl != null && !cachedUrl.trim().isEmpty()) {
			apiQuery = cachedUrl;
		}
	}

	
	public List<Status> search() throws TwitterException
	{
		int counter = 0;
		List<Status> tweets = new ArrayList<Status>();
		SearchResult searchResult = null;
		do {
			searchResult = requestSearchAPI();
			tweets.addAll(searchResult.getTweets());
			
			if (counter == 0) { 
				cachedUrl = searchResult.getRefreshUrl();
				OAuth2ConfigManager.getInstance().saveCachedUrl(cachedUrl);
			}
			
			if (searchResult.hasNextResults()) {
				apiQuery = searchResult.getNextResults();
			}
			logger.info(searchResult.toString());	
			counter ++;			
		} while((counter < MAX_REQUESTS) && searchResult.hasNextResults());
		logger.info("Twitter SearchAPI requested {} times, and returned {} tweets.", counter--, tweets.size());
		
		return tweets;
	}
	
	
	private SearchResult requestSearchAPI() throws TwitterException {
		HttpResponse response;
		Map<String,String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("User-Agent", "twittercity.org Application / mailto:administration@twittercity.org");
		requestHeaders.put("Authorization","Bearer " + OAuth2.getBearerToken());
		try {
			HttpRequest httpRequest = new HttpRequest((API_URL + apiQuery), "GET", requestHeaders);
			response = httpRequest.handleRequest();
			
		} catch (TwitterException te) {
			if (te.getResponseCode() == HttpResponseCode.UNAUTHORIZED){
				logger.warn("Saved access token is invalid, requesting a new one.");
				//Request a new Bearer Token and try to search again.
				requestHeaders.replace("Authorization","Bearer " + OAuth2.getNewBearerToken());
				HttpRequest httpRequest = new HttpRequest((API_URL + cachedUrl), "GET", requestHeaders);
				response = httpRequest.handleRequest();	
			}
			else{
				throw te;
			}	
		}		
		return new SearchResult(response);	
	}	
}

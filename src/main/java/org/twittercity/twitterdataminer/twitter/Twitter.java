package org.twittercity.twitterdataminer.twitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.database.dao.ApplicationStateDataDAO;
import org.twittercity.twitterdataminer.database.dao.QueryDAO;
import org.twittercity.twitterdataminer.database.dao.StatusDAO;
import org.twittercity.twitterdataminer.http.HttpParameter;
import org.twittercity.twitterdataminer.http.HttpRequest;
import org.twittercity.twitterdataminer.http.HttpResponse;
import org.twittercity.twitterdataminer.http.HttpResponseCode;
import org.twittercity.twitterdataminer.http.RequestMethod;
import org.twittercity.twitterdataminer.oauth2.OAuth2;
import org.twittercity.twitterdataminer.twitter.models.Query;
import org.twittercity.twitterdataminer.twitter.models.Status;
import org.twittercity.twitterdataminer.utilities.json.ParseUtil;

public class Twitter {
	private static Logger logger = LoggerFactory.getLogger(Twitter.class);
	
	private static final String API_URL = "https://api.twitter.com/1.1/search/tweets.json";
	
	private static int remainingRequests = 0;
	
	private static int countingTweets = 0;
	
	private static int totalRequestCounter = 0;
	
	private Twitter() {
	}

	public static void searchAndSave() {
		// Used to avoid infinity loop when no new tweets are available
		int counter = 1; 
		int totalQueries = QueryDAO.countQueries();
		
		try { 
		  remainingRequests = getRemainingRequests(); 
		} 
		catch (TwitterException e) { 
			logger.error(e.getMessage()); 
			return; 
		}
		while(remainingRequests > 0 && totalQueries >= counter) {
			try {
				search(QueryDAO.getQueryById(ApplicationStateDataDAO.getLastSearchedQueryId()));
				counter++;
			} catch (TwitterException e) {
				logger.error(e.getMessage());
				return;
			}
		}
	}
	
	private static void search(Query query) throws TwitterException
	{
		int requestCounter = 0;
		boolean moreTweetsAvailable = true;
		
		while((requestCounter < remainingRequests && moreTweetsAvailable)) {
			
			moreTweetsAvailable = requestAndprocessTweets(query);

			logger.info("Request for tweets no. {}.", totalRequestCounter);
			requestCounter++;
			totalRequestCounter++;
		}

		remainingRequests -= requestCounter;
		countingTweets += requestCounter * query.getCount();
		logger.info("Twitter SearchAPI requested {} times, and returned {} tweets.", totalRequestCounter, countingTweets);
	}
	

	/**
	 * Requests and Process(filter, save etc) the tweets returned from Twitter
	 * @param query The query that will be used to search for tweets
	 * @return true if there are more tweets for the same query
	 * @throws TwitterException If an HTTP error is occurred
	 */
	private static boolean requestAndprocessTweets(Query query) throws TwitterException {
		
		SearchResult searchResult = requestSearchAPI(query);
		boolean areThereNextResultsAvailable = true;
		// Update refreshSinceId if needed
		query.updateRefreshSinceIDFromURL(searchResult.getRefreshUrl());
					
		if (searchResult.hasNextResults()) {
			query.setNextResultsIDFromURL(searchResult.getNextResults());
		} else if(!searchResult.hasNextResults() || query.getLatestProcessedTweetId() >= query.getNextResultsId()) {
			areThereNextResultsAvailable = false;
			logger.debug("There is no more paginated results reseting nextResults or tweets already processed!");
			query.resetNextResults();
			
			int lastSearchedQueryId = 1;
			Query nextQuery = QueryDAO.getNextQuery(query.getId());
			if(nextQuery != null) {
				lastSearchedQueryId = nextQuery.getId();
			}
			
			// Update application state in order to search for the next query in the next request
			ApplicationStateDataDAO.saveLastSearchedQueryId(lastSearchedQueryId);
		}
		
		// Assign query's feeling in every tweet.
		List<Status> resultTweets = searchResult.getTweets();
		StatusFilter.filterTweets(resultTweets);
		resultTweets.forEach(tweet -> {
			tweet.setQuery(query);
			tweet.setFeeling(query.getFeeling());
		});
		StatusDAO.saveTweets(resultTweets);
		QueryDAO.updateQuery(query);
		return areThereNextResultsAvailable;
	}

	
	/**
	 * Builds and issues the request to the Twitter Search API
	 * @param query Query used to build the HTTP request
	 * @return SearchResult Object representing the Search Results from Twitter
	 * @throws TwitterException If an HTTP error is occurred
	 */
	private static SearchResult requestSearchAPI(Query query) throws TwitterException {
		HttpResponse response;
		Map<String,String> requestHeaders = new HashMap<>();
		requestHeaders.put("User-Agent", "twittercity.org Application / mailto:administration@twittercity.org");
		requestHeaders.put("Authorization", "Bearer " + OAuth2.getBearerToken());
		try {
			HttpRequest httpRequest = new HttpRequest(RequestMethod.GET, API_URL, query.asHttpParameterArray(), requestHeaders);
			response = httpRequest.handleRequest();
		} catch (TwitterException te) {
			if (te.getResponseCode() == HttpResponseCode.UNAUTHORIZED){
				logger.warn("Saved access token is invalid, requesting a new one.");
				//Request a new Bearer Token and try to search again.
				requestHeaders.replace("Authorization","Bearer " + OAuth2.getNewBearerToken());
				HttpRequest httpRequest = new HttpRequest(RequestMethod.GET, API_URL, query.asHttpParameterArray(), requestHeaders);
				response = httpRequest.handleRequest();	
			}
			else{
				throw te;
			}	
		}		
		return new SearchResult(response);	
	}
	
	/**
	 * Requests the /rate_limit_status endpoint to check how many requests we have left to avoid being rate limited
	 * @throws TwitterException
	 */
	public static int getRemainingRequests() throws TwitterException {
		HttpResponse response;
		Map<String,String> requestHeaders = new HashMap<>();
		requestHeaders.put("User-Agent", "twittercity.org Application / mailto:administration@twittercity.org");
		requestHeaders.put("Authorization","Bearer " + OAuth2.getBearerToken());
		HttpParameter[] param = {new HttpParameter("resources", "search")};
		HttpRequest httpRequest = 
				new HttpRequest(
						RequestMethod.GET, 
						"https://api.twitter.com/1.1/application/rate_limit_status.json", 
						param, 
						requestHeaders
					);
		try {
			response = httpRequest.handleRequest();
		} catch (TwitterException te) {
			if (te.getResponseCode() == HttpResponseCode.UNAUTHORIZED){
				logger.warn("Saved access token is invalid, requesting a new one.");
				//Request a new Bearer Token and try to search again.
				requestHeaders.replace("Authorization","Bearer " + OAuth2.getNewBearerToken());
				httpRequest.setRequestHeaders(requestHeaders);
				response = httpRequest.handleRequest();	
			}
			else{
				throw te;
			}	
		}		
		JSONObject json = response.asJSONObject().getJSONObject("resources").getJSONObject("search").getJSONObject("/search/tweets");
		int remainingRequests = ParseUtil.getInt("remaining", json);
		logger.info("Remaining requests before getting rated limited are: {}", remainingRequests);
		return remainingRequests;
	}
}

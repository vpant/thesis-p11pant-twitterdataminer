package org.twittercity.twitterdataminer.searchtwitter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.http.HttpResponse;
import org.twittercity.twitterdataminer.json.ParseUtil;

/**
 *  Data object tha represents a Result for SearchAPI request
 *
 */

public class SearchResult {

	private String refreshUrl;
	private String nextResults;
	private List<Status> tweets;
	
	public SearchResult(HttpResponse res) throws TwitterException
	{
		try {
			JSONObject json = res.asJSONObject();
			
			refreshUrl = ParseUtil.getString("refresh_url", json.getJSONObject("search_metadata"));
			nextResults = ParseUtil.getString("next_results", json.getJSONObject("search_metadata"));

			JSONArray array = json.getJSONArray("statuses");
			tweets = new ArrayList<Status>(array.length());
			
			for (int i = 0; i < array.length(); i++) {
				JSONObject tweet = array.getJSONObject(i);
				tweets.add(new Status(tweet));
			}
		} catch (TwitterException te) {
			throw new TwitterException(te.getMessage());
		}
	}
	
	public boolean hasNextResults()
	{
		return (nextResults != null) ? true : false;	
	}
		
	public String getRefreshUrl() {
		return refreshUrl;
	}

	public String getNextResults() {
		return nextResults;
	}

	public List<Status> getTweets() {
		return tweets;
	}
	
	@Override
	public String toString()
	{
		return "Refresh Url: " + refreshUrl
				+ "Next Results: " + nextResults;	
	}
}

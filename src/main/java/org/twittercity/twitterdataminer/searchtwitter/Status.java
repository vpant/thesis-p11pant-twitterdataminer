package org.twittercity.twitterdataminer.searchtwitter;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.json.ParseUtil;

/**
 * Data class representing a single tweet of a user.
 */

public class Status {
	//private static final Logger logger = LoggerFactory.getLogger(Tweet.class);

	private String id;
	private String createdAt;
	private String text;
	private boolean isRetweeted;
	private String lang;
	private String twitterAccountName;
	private String twitterAccountID;
	//private String tweetUrl; // twitter.com/twitter-user-screen-name/status/tweet-id-str
	
	public Status(JSONObject json) throws TwitterException
	{
		id = ParseUtil.getString("id_str", json);
		createdAt = ParseUtil.getDate("created_at", json);
		isRetweeted = ParseUtil.getBoolean("retweeted", json);
		lang = ParseUtil.getString("lang", json);
		text = ParseUtil.getString("text", json);
		
		twitterAccountName = ParseUtil.getString("screen_name", json.getJSONObject("user"));
		twitterAccountID = ParseUtil.getString("id_str", json.getJSONObject("user"));
	}
	
	public Status(String id, String createdAt, String text, String twitterAccountName, String twitterAcountID)
	{
		this(id, createdAt, text, twitterAccountName, twitterAcountID, false, "en");
	}
	
	public Status (String id, String createdAt, String text, String twitterAccountName, String twitterAcountID, boolean isRetweeted, String lang)
	{
		this.id = id;
		this.createdAt = createdAt;
		this.text = text;
		this.isRetweeted = isRetweeted;
		this.lang = lang;
		this.twitterAccountName = twitterAccountName;
		this.twitterAccountID = twitterAcountID;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public boolean isRetweeted() {
		return isRetweeted;
	}

	public String getLang() {
		return lang;
	}

	public String getTwitterAccountName() {
		return twitterAccountName;
	}

	public String getTwitterAccountID() {
		return twitterAccountID;
	}
	
	@Override
	public String toString()
	{
		return "User " + twitterAccountName 
				+ " with ID " + twitterAccountID 
				+ " said: " + text 
				+ " at " + createdAt + "\n";
	}
}

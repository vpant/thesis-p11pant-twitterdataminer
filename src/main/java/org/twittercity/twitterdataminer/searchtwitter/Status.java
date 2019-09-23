package org.twittercity.twitterdataminer.searchtwitter;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.json.ParseUtil;

/**
 * Data class representing a single tweet of a user.
 */
@Entity
@Table(name = "tweets")
public class Status {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbId;
	@Column(name = "twitter_tweet_id")
	private String tweetID;
	@Column(name = "date")
	private String createdAt;
	@Column(name = "text")
	private String text;
	@Column(name = "author")
	private String twitterAccountName;
	@Column(name = "id_str")
	private String twitterAccountID;
	@Column(name = "profile_pic_url")
	private String profilePicUrl;
	@Column(name = "feeling")
	private int feelingID = -1;
	//private String tweetUrl; // twitter.com/{twitter-user-screen-name}/status/{tweet-id-str}
	@Transient
	private boolean isRetweeted;
	@Transient
	private String lang;
	
	public Status(JSONObject json) throws TwitterException
	{
		tweetID = ParseUtil.getString("id_str", json);
		createdAt = ParseUtil.getDate("created_at", json);
		isRetweeted = ParseUtil.getBoolean("retweeted", json);
		lang = ParseUtil.getString("lang", json);
		text = ParseUtil.getString("text", json);
		
		twitterAccountName = ParseUtil.getString("screen_name", json.getJSONObject("user"));
		twitterAccountID = ParseUtil.getString("id_str", json.getJSONObject("user"));
		// Should assign feeling here
		
		profilePicUrl = ParseUtil.getString("profile_image_url_https", json.getJSONObject("user"));
	}
	
	public Status(String id, String createdAt, String text, String twitterAccountName, String twitterAcountID, String profilePicUrl, int feelingID) {
		this(id, createdAt, text, twitterAccountName, twitterAcountID, profilePicUrl, false, "en", feelingID);
	}
	
	public Status (String tweetID, String createdAt, String text, String twitterAccountName, String profilePicUrl, String twitterAcountID, boolean isRetweeted, String lang, int feelingID) {
		this.tweetID = tweetID;
		this.createdAt = createdAt;
		this.text = text;
		this.isRetweeted = isRetweeted;
		this.lang = lang;
		this.twitterAccountName = twitterAccountName;
		this.twitterAccountID = twitterAcountID;
		this.profilePicUrl = profilePicUrl;
		this.feelingID = feelingID;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}

	public String getTweetID() {
		return tweetID;
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
	
	public String getProfilePicUrl() {
		return profilePicUrl;
	}
	
	public int getFeeling() {
		return feelingID;
	}

	public void setFeeling(int feelingID) {
		this.feelingID = feelingID;
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

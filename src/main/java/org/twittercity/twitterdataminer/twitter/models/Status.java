package org.twittercity.twitterdataminer.twitter.models;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.database.FeelingEnumConverter;
import org.twittercity.twitterdataminer.utilities.json.ParseUtil;

/**
 * Data class representing a single tweet of a user.
 */
@Entity
@Table(name = "tweet")
public class Status {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbId;
	@Column(name = "twitter_tweet_id", unique = true)
	private String tweetID;
	@Column(name = "date")
	private String createdAt;
	@Column(name = "text")
	private String text;
	@Column(name = "author")
	private String twitterAccountName;
	@Column(name = "author_account_id")
	private String twitterAccountID;
	@Column(name = "profile_pic_url")
	private String profilePicUrl;
	@OneToOne()
	@JoinColumn(name = "state", referencedColumnName = "id")
	private State state;
	@Column(name = "feeling")
	@Convert(converter = FeelingEnumConverter.class)
	private Feeling feeling = Feeling.NO_FEELING;
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
		
		state = State.fromJson(json);
		
		
	}
	
	public Status() { }
	
	public String getCreatedAt() {
		return createdAt;
	}

	public String getTweetID() {
		return tweetID;
	}

	public String getText() {
		return text;
	}

	public State getState() {
		return this.state;
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
	
	public Feeling getFeeling() {
		return feeling;
	}

	public void setFeeling(Feeling feeling) {
		this.feeling = feeling;
	}
	
	public void setText(String text) {
		this.text= text ;
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

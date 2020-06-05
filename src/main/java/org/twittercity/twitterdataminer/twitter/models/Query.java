package org.twittercity.twitterdataminer.twitter.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.database.FeelingEnumConverter;
import org.twittercity.twitterdataminer.database.QueryFiltersConverter;
import org.twittercity.twitterdataminer.http.HttpParameter;

@Entity
@Table(name = "search_query")
public class Query {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbId;
	@Column(name = "keywords")
	private String keywords = "";
	/** Restricts tweets to the given language, given by an ISO 639-1 code */
	@Column(name = "lang")
	private String lang = "";
	/** The locale of the tweets we want. */
	@Column(name = "locale")
	private String locale = "";
	/** Used to tell how many tweets we want per request. Defaults to 15 */
	@Column(name = "count")
	private int count = -1;
	/**  Used to retrieve tweets with id greater than since_id value */
	@Column(name = "since_id")
	private long sinceId = -1;
	/**  Used to retrieve tweets with id lower or equal to max_id value */
	@Column(name = "max_id")
	private long maxId = -1;
	/** Used to limit tweets by users located within a given radius of the given latitude/longitude. */
	@Column(name = "geocode")
	private String geocode = "";
	@Column(name = "result_type")
	private ResultType resultType = null;
	/** Holds the since_id value to exclude previous processed tweets */
	@Column(name = "refresh_since_id")
	private long refreshSinceId = -1; // Query string to get next chronological results
	/**	Holds the biggest tweet_id that has already been processed 
	 * (including the paginated tweets after it) to avoid re-saving duplicate tweets. 
	 * When using the refresh_since_id to get the next results, the response includes paginated tweets
	 * that we already have processed and saved.
	 * */
	@Column(name = "latest_processed_tweet_id")
	private long latestProcessedTweetId = -1; 
	/** If there are next results this will hold the maxId value to use in order to retrieve them */
	@Column(name = "next_results_max_id") 
	private long nextResultsMaxId = -1; // Query string to get next reverse-chronological results
	@Column(name = "filters")
	@Convert(converter = QueryFiltersConverter.class)
	private List<String> filters = new ArrayList<>();
	@Column(name = "feeling")
	@Convert(converter = FeelingEnumConverter.class)
	private Feeling feeling = Feeling.NO_FEELING;
	
	/**
	 * Creates a Query object from a url
	 *
	 * @param String to build the query from
	 * @return new query instance
	 */
	public static Query createQuery(String url) {
		Query query = new Query();
		
		if (url != null) {
			Map<String, String> params = urlParamatersToMap(url);

			if (params.containsKey("q")) {
				String queryWithFilters = params.get("q");
				for (String pair : queryWithFilters.split("-")) {
					String[] parts = pair.split(":", 2);
					if(parts.length == 2) {
		               String value = parts[1];
		                if(!"".equals(value))
		                	query.addFilter(value);
		            }
				}
				// Probably bug. params.get("q") should contain the filters as well
				query.setKeywords(params.get("q"));
			}
			if (params.containsKey("since_id"))
				query.setSinceId(Long.parseLong(params.get("since_id")));
			if (params.containsKey("lang"))
				query.setLang(params.get("lang"));
			if (params.containsKey("locale"))
				query.setLocale(params.get("locale"));
			if (params.containsKey("max_id"))
				query.setMaxId(Long.parseLong(params.get("max_id")));
			if (params.containsKey("count"))
				query.setCount(Integer.parseInt(params.get("count")));
			if (params.containsKey("result_type")) {
				query.setResultType(Query.ResultType.valueOf(params.get("result_type")));
			}
		}
		
		LoggerFactory.getLogger(Query.class).debug(query.toString());
		
		return query;
	} 

	private static Map<String, String> urlParamatersToMap(String urlString) {
		String queryStringParameters = urlString.substring(1, urlString.length()); // The url without the leading ?

		Map<String, String> params = new LinkedHashMap<>();
		for (HttpParameter param : HttpParameter.decodeParameters(queryStringParameters)) {
			// Yes, we'll overwrite duplicate parameters, but we should not
			// get duplicate parameters from this endpoint.
			params.put(param.getName(), param.getValue());
		}
		return params;
	}

	public Query() {}
	
	public int getId() {
		return this.dbId;
	}
	
	public long getLatestProcessedTweetId() {
		return latestProcessedTweetId;
	}

	public void setLatestProcessedTweetId(long latestProcessedTweetId) {
		this.latestProcessedTweetId = latestProcessedTweetId;
	}
		
	/**
	 * Sets the keywords string
	 *
	 * @param keywords the keywords string
	 * @return the instance
	 */
	public Query keywords(String keywords) {
		setKeywords(keywords);
		return this;
	}

	/**
	 * Returns the specified keywords
	 *
	 * @return keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * Sets the keywords string
	 *
	 * @param keywords the keywords string
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Query geocode(String geocode) {
		setGeocode(geocode);
		return this;
	}
	
	/**
	 * Returns the geocode field
	 *
	 * @return geocode
	 */
	public String getGeocode() {
		return geocode;
	}

	/**
	 * Sets the geocode string
	 *
	 * @param geocode the keywords string
	 */
	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}

	
	
	public Query filter(String filter) {
		addFilter(filter);
		return this;
	}

	public List<String> getFilters() {
		return this.filters;
	}

	public void addFilter(String filter) {
		this.filters.add(filter);
	}
	
	public Query filters(List<String> filters) {
		this.filters.addAll(filters);
		return this;
	}
	
	/**
	 * Resets nextResultsMaxId parameter to default value. Used to update query 
	 * when there are no other paginated results.
	 */
	public void resetNextResults() {
		this.nextResultsMaxId = -1;
		// Setting
		this.latestProcessedTweetId = this.sinceId;
	}
	
	/**
	 * Extract the max_id from the next_results url and assign the value. The value reverts to default if 
	 * max_id value cannot be find in the url.
	 * 
	 * @param nextResultsUrl The next_results url returned from Twitter in search_metadata area.
	 */
	public void setNextResultsIDFromURL(String nextResultsUrl) {
		if(nextResultsUrl == null || "".equals(nextResultsUrl)) {
			this.nextResultsMaxId = -1;
			return;
		}
		Map<String, String> params = urlParamatersToMap(nextResultsUrl);
		if (params.containsKey("max_id")) {
			this.nextResultsMaxId = Long.parseLong(params.get("max_id"));
		}
		else {
			this.nextResultsMaxId = -1;
		}
		
	}
	
	
	/**
	 * Extract the since_id from the refresh_url url and assign the value. 
	 * 
	 * @param refreshUrl The refresh_url url returned from Twitter in search_metadata area.
	 */
	public void setRefreshSinceID(Long refreshSinceId) {
		this.refreshSinceId = refreshSinceId;
	}
	
	public long getRefreshSinceID() {
		return this.refreshSinceId;
	}
	
	public long getNextResultsId() {
		return nextResultsMaxId;
	}
	
	/*
	 * If the refresh url contains a since_id greater than the one we already have,
	 * we keep it instead.
	 */
	public void updateRefreshSinceIDFromURL(String refreshUrl) {
		if(refreshUrl == null || "".equals(refreshUrl)) {
			return;
		}
		long sinceIdExtracted = this.refreshSinceId;
		Map<String, String> params = urlParamatersToMap(refreshUrl);
		if (params.containsKey("since_id")) {
			sinceIdExtracted = Long.parseLong(params.get("since_id"));
		}	
		this.refreshSinceId =  (this.refreshSinceId < sinceIdExtracted) ? sinceIdExtracted : this.refreshSinceId;
	}
	
	/**
	 * restricts tweets to the given language, given by an
	 * <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 *
	 * @param lang an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1
	 *             code</a>
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * restricts tweets to the given language, given by an
	 * <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1 code</a>
	 *
	 * @param lang an <a href="http://en.wikipedia.org/wiki/ISO_639-1">ISO 639-1
	 *             code</a>
	 * @return the instance
	 */
	public Query lang(String lang) {
		setLang(lang);
		return this;
	}

	public Query feeling(Feeling feeling) {
		setFeeling(feeling);
		return this;
	}
	
	public void setFeeling(Feeling feeling) {
		this.feeling = feeling;
	}
	

	public Feeling getFeeling() {
		return feeling;
	}

	
	/**
	 * Returns the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the default
	 * should work in the majority of cases.
	 *
	 * @return locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * Specify the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the default
	 * should work in the majority of cases.
	 *
	 * @param locale the locale
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Specify the language of the query you are sending (only ja is currently
	 * effective). This is intended for language-specific clients and the default
	 * should work in the majority of cases.
	 *
	 * @param locale the locale
	 * @return the instance
	 */
	public Query locale(String locale) {
		setLocale(locale);
		return this;
	}
	
	/**
	 * Use to determine if this query has next results
	 * @return true if there is an max_id used to retrieve the next results
	 */
	public boolean hasNextResults() {
		return nextResultsMaxId > 0;
	}
	
	/**
	 * Returns tweets with status ids less than the given id.
	 *
	 * @return maxId
	 */
	public long getMaxId() {
		return maxId;
	}

	/**
	 * If specified, returns tweets with status ids less than the given id.
	 *
	 * @param maxId maxId
	 */
	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	/**
	 * If specified, returns tweets with status ids less than the given id.
	 *
	 * @param maxId maxId
	 * @return this instance
	 */
	public Query maxId(long maxId) {
		setMaxId(maxId);
		return this;
	}


	/**
	 * Returns the number of tweets to return per page, up to a max of 100
	 *
	 * @return count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * sets the number of tweets to return per page, up to a max of 100
	 *
	 * @param count the number of tweets to return per page
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * sets the number of tweets to return per page, up to a max of 100
	 *
	 * @param count the number of tweets to return per page
	 * @return the instance
	 */
	public Query count(int count) {
		setCount(count);
		return this;
	}
	
	/**
	 * returns sinceId
	 *
	 * @return sinceId
	 */
	public long getSinceId() {
		return sinceId;
	}

	/**
	 * returns tweets with status ids greater than the given id.
	 *
	 * @param sinceId returns tweets with status ids greater than the given id
	 */
	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}

	/**
	 * returns tweets with status ids greater than the given id.
	 *
	 * @param sinceId returns tweets with status ids greater than the given id
	 * @return the instance
	 */
	public Query sinceId(long sinceId) {
		setSinceId(sinceId);
		return this;
	}

	/**
	 * Returns resultType
	 *
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * Default value is Query.MIXED if parameter not specified
	 *
	 * @param resultType Query.MIXED or Query.POPULAR or Query.RECENT
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * If specified, returns tweets included popular or real time or both in the
	 * response
	 *
	 * @param resultType resultType
	 * @return the instance
	 */
	public Query resultType(ResultType resultType) {
		setResultType(resultType);
		return this;
	}

	private static final HttpParameter WITH_TWITTER_USER_ID = new HttpParameter("with_twitter_user_id", "true");

	public HttpParameter[] asHttpParameterArray() {
		ArrayList<HttpParameter> params = new ArrayList<>(12);
		StringBuilder queryWithFilters = new StringBuilder(keywords);
		for(String filter : filters) {
			queryWithFilters.append(" -filter:" + filter);
		}
		appendParameter("q", queryWithFilters.toString(), params);
		appendParameter("lang", lang, params);
		appendParameter("locale", locale, params);
		appendParameter("count", count, params);
		if(nextResultsMaxId <= 0) {
			if(refreshSinceId > 0) {
				appendParameter("since_id", refreshSinceId, params);
			} else {
				appendParameter("since_id", sinceId, params);
			}
		} 
		else {
			appendParameter("max_id", nextResultsMaxId, params);
		}
		appendParameter("geocode", geocode, params);
		if (resultType != null) {
			params.add(new HttpParameter("result_type", resultType.name()));
		}
		params.add(WITH_TWITTER_USER_ID);
		HttpParameter[] paramArray = new HttpParameter[params.size()];
		return params.toArray(paramArray);
	}

	private void appendParameter(String name, String value, List<HttpParameter> params) {
		if (value != null && !"".equals(value)) {
			params.add(new HttpParameter(name, value));
		}
	}

	private void appendParameter(String name, long value, List<HttpParameter> params) {
		if (0 <= value) {
			params.add(new HttpParameter(name, String.valueOf(value)));
		}
	}

	public enum ResultType {
		POPULAR, MIXED, RECENT
	}

	@Override
    public String toString() {
        return "Query{" +
        		"dbID='" + dbId + '\'' +
        		"query='" + keywords + '\'' +
                ", filters='" + filters.toString() + '\'' +
                ", lang='" + lang + '\'' +
                ", locale='" + locale + '\'' +
                ", count=" + count +
                ", sinceId=" + sinceId +
                ", maxId=" + maxId +
                ", geocode='" + geocode + '\'' +
                ", resultType='" + resultType + '\'' +
                ", nextResultsMaxId='" + nextResultsMaxId + '\'' +
                ", refreshUrl='" + refreshSinceId + '\'' +
                '}';
    }	
	
}

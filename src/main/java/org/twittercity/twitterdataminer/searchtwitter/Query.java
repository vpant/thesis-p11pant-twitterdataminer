package org.twittercity.twitterdataminer.searchtwitter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.twittercity.twitterdataminer.http.HttpParameter;

public class Query {
	private String query = null;
	private String lang = null;
	private String locale = null;
	private long maxId = -1l;
	private int count = -1;
	private String since = null;
	private long sinceId = -1;
	private String geocode = null;
	private ResultType resultType = null;
	private String nextPageQuery = null;
	private ArrayList<String> filters = new ArrayList<String>();
	
	public static Query createWithNextPageQuery(String nextPageQuery) {
		Query query = new Query();
		query.nextPageQuery = nextPageQuery;

		if (nextPageQuery != null) {
			String nextPageParameters = nextPageQuery.substring(1, nextPageQuery.length());

			Map<String, String> params = new LinkedHashMap<String, String>();
			for (HttpParameter param : HttpParameter.decodeParameters(nextPageParameters)) {
				// Yes, we'll overwrite duplicate parameters, but we should not
				// get duplicate parameters from this endpoint.
				params.put(param.getName(), param.getValue());
			}

			if (params.containsKey("q")) {
				String queryWithFilters = params.get("q");
				for (String pair : queryWithFilters.split("-")) {
					String[] parts = pair.split(":", 2);
					if(parts.length == 2) {
		               String value = parts[1];
		                if(!value.equals(""))
		                	query.addFilter(value);
		            }
				}
				query.setQuery(params.get("q"));
			}
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

		return query;
	}

	/**
	 * Sets the query string
	 *
	 * @param query the query string
	 * @return the instance
	 */
	public Query query(String query) {
		setQuery(query);
		return this;
	}

	/**
	 * Returns the specified query
	 *
	 * @return query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the query string
	 *
	 * @param query the query string
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	
	public Query filter(String filter) {
		addFilter(filter);
		return this;
	}

	public ArrayList<String> getFilters() {
		return this.filters;
	}

	public void addFilter(String filter) {
		this.filters.add(filter);
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

	public String nextPage() {
		return nextPageQuery;
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
	 * Returns tweets with since the given date. Date should be formatted as
	 * YYYY-MM-DD
	 *
	 * @return since
	 */
	public String getSince() {
		return since;
	}

	/**
	 * If specified, returns tweets with since the given date. Date should be
	 * formatted as YYYY-MM-DD
	 *
	 * @param since since
	 */
	public void setSince(String since) {
		this.since = since;
	}

	/**
	 * If specified, returns tweets with since the given date. Date should be
	 * formatted as YYYY-MM-DD
	 *
	 * @param since since
	 * @return since
	 */
	public Query since(String since) {
		setSince(since);
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
		ArrayList<HttpParameter> params = new ArrayList<HttpParameter>(12);
		StringBuilder queryWithFilters = new StringBuilder(query);
		for(String filter : filters) {
			queryWithFilters.append("-filter:" + filter);
		}
		appendParameter("q", queryWithFilters.toString(), params);
		appendParameter("lang", lang, params);
		appendParameter("locale", locale, params);
		appendParameter("count", count, params);
		appendParameter("since", since, params);
		appendParameter("since_id", sinceId, params);
		appendParameter("geocode", geocode, params);
		if (resultType != null) {
			params.add(new HttpParameter("result_type", resultType.name()));
		}
		params.add(WITH_TWITTER_USER_ID);
		HttpParameter[] paramArray = new HttpParameter[params.size()];
		return params.toArray(paramArray);
	}

	private void appendParameter(String name, String value, List<HttpParameter> params) {
		if (value != null) {
			params.add(new HttpParameter(name, value));
		}
	}

	private void appendParameter(String name, long value, List<HttpParameter> params) {
		if (0 <= value) {
			params.add(new HttpParameter(name, String.valueOf(value)));
		}
	}

	public enum ResultType {
		popular, mixed, recent
	}

	@Override
    public String toString() {
        return "Query{" +
                "query='" + query + '\'' +
                ", filters='" + filters.toString() + '\'' +
                ", lang='" + lang + '\'' +
                ", locale='" + locale + '\'' +
                ", maxId=" + maxId +
                ", count=" + count +
                ", since='" + since + '\'' +
                ", sinceId=" + sinceId +
                ", geocode='" + geocode + '\'' +
                ", resultType='" + resultType + '\'' +
                ", nextPageQuery='" + nextPageQuery + '\'' +
                '}';
    }
}

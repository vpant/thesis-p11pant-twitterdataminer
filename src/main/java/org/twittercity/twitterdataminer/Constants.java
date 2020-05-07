package org.twittercity.twitterdataminer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.twittercity.twitterdataminer.twitter.models.Query.ResultType;

/**
 * This class will hold global constants for our program. For example the query parameters that will be used 
 * for initializing every query object.
 *
 */
public class Constants {
	
	private Constants() {}
	
	/*
	 * Constants used in query creation
	 */
	public static final int KEYWORDS_GROUP_COUNT = 1;//8;
	public static final String KEYWORDS_LOGICAL_OPERATOR = "OR";
	public static final int COUNT = 100;
	public static final ResultType RESULT_TYPE = ResultType.MIXED;
	public static final String LANG = "en";
	public static final String GEO = "39.8,-95.583068847656,2500km";
	public static final List<String> FILTERS = new ArrayList<>(Arrays.asList(
		    "links",
		    "replies",
		    "retweets",
		    "images",
		    "videos"
		));	
}

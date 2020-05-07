package org.twittercity.twitterdataminer.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.twittercity.twitterdataminer.Constants;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.database.dao.QueryDAO;
import org.twittercity.twitterdataminer.twitter.models.Feeling;
import org.twittercity.twitterdataminer.twitter.models.Query;
import org.twittercity.twitterdataminer.utilities.json.ParseUtil;


public class Keywords {

	private Keywords() {
	}

	
	/**
	 * Reads the keywords.json file located in the same folder as the jar and stores the keywords in the database
	 * @throws TwitterException
	 */
	public static void buildTable() throws TwitterException {
		if(!QueryDAO.isQueryTableEmpty()) {
			return;
		}
		
		try {
			
			String path = new File(QueryDAO.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
			if (path != null && !("".equals(path))) {
				path += File.separator + "keywords.json";
			}

			InputStream is = new FileInputStream(path);
			JSONObject jsonObject = new JSONObject(new JSONTokener(is));
			
			EnumMap<Feeling, List<String>> feelingKeywordsMap = initializeKeywordsMap();
			
			jsonObject.keys().forEachRemaining(key -> {
				Feeling feeling = Feeling.getByName(ParseUtil.getString(key, jsonObject));
				if(feeling.equals(Feeling.NO_FEELING)) {
					return;
				}
			    addToMap(feelingKeywordsMap, feeling, key);
			});
			
			List<Query> queries = new ArrayList<>();			
			feelingKeywordsMap.forEach((feeling, keywords) -> {
				groupKeywordsAndCreateQuery(queries, keywords, feeling);
			});
			
			QueryDAO.saveQueries(queries);
			
		} catch (FileNotFoundException | URISyntaxException e) {
			throw new TwitterException("Could not open the keywords.json file.");
		}

	}

	
	/**
	 * Groups keywords and constructs/adds the appropriate queries to queries list
	 * @param queries The list that the new queries will be added to
	 * @param keywords List of keywords for the feeling
	 * @param feeling Feeling enum value that expressed from the keywords
	 */
	private static void groupKeywordsAndCreateQuery(List<Query> queries, List<String> keywords, Feeling feeling) {
		int counter = 0;
		Query newQuery = null;
		String queryKeywords = "";
		String keywordSeparator = " " + Constants.KEYWORDS_LOGICAL_OPERATOR + " ";
		for(String keyword : keywords ) {
			if(counter >= Constants.KEYWORDS_GROUP_COUNT) {
				if(newQuery != null) {
					queries.add(newQuery.keywords(queryKeywords));
				}
				newQuery = new Query()
						.count(Constants.COUNT)
						.resultType(Constants.RESULT_TYPE)
						.lang(Constants.LANG)
						.filters(Constants.FILTERS)
						.geocode(Constants.GEO)
						.feeling(feeling);
				queryKeywords = "";
				counter = 0;
			}
			queryKeywords += ( (counter == 0 ? "" : keywordSeparator) + keyword);
			counter++;
		}
		if(newQuery != null) {
			queries.add(newQuery.keywords(queryKeywords));
		}
	}	

	private static void addToMap(EnumMap<Feeling, List<String>> feelingKeywordsMap, Feeling feeling, String keyword) {
		feelingKeywordsMap.get(feeling).add(keyword);
	}

	
	/**
	 * Initialize a HashMap and all the Feeling enumerations as keys
	 * @return The HashMap
	 */
	private static EnumMap<Feeling, List<String>> initializeKeywordsMap() {
		EnumMap<Feeling, List<String>> feelingKeywordsMap = new EnumMap<>(Feeling.class);
		for (Feeling feeling : Feeling.values()) {
			if(feeling.equals(Feeling.NO_FEELING)) {
				continue;
			}
			feelingKeywordsMap.put(feeling, new ArrayList<String>());
		}
		return feelingKeywordsMap;
	}
	
}

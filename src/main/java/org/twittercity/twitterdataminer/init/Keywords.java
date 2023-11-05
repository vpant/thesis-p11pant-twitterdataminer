package org.twittercity.twitterdataminer.init;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.twittercity.twitterdataminer.Constants;
import org.twittercity.twitterdataminer.TwitterDataMiner;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.database.dao.QueryDAO;
import org.twittercity.twitterdataminer.twitter.models.Feeling;
import org.twittercity.twitterdataminer.twitter.models.Query;
import org.twittercity.twitterdataminer.utilities.json.ParseUtil;


public class Keywords {

	private Keywords() { }

	/**
	 * Reads the keywords.json file located in the same folder as the jar and stores the keywords in the database
	 * @throws TwitterException
	 */
	public static void buildTable() throws TwitterException {
		if(!QueryDAO.isQueryTableEmpty()) {
			return;
		}
		
		try {
			
//			String path = new File(Query.class.getProtectionDomain().getCodeSource().getLocation().toURI())
//					.getParent();
//			if (path != null && !("".equals(path))) {
//				path += File.separator + "keywords.json";
//			}

			URL pathUrl = TwitterDataMiner.class.getClassLoader().getResource("keywords.json");

			
			Map<String, List<String>> feelingKeywordsJson = TwitterDataMiner.objectMapper.readValue(pathUrl, Map.class);

			Map<Feeling, List<String>> feelingKeywordsMap = initializeKeywordsMap();

			feelingKeywordsJson.forEach((feelingString, keywords) -> {
				Feeling feeling = Feeling.getByName(feelingString);

				// Keyword:  feeling in the json
				if(feeling.equals(Feeling.NO_FEELING)) {
					return;
				}

				keywords.forEach(keyword -> addToMap(feelingKeywordsMap, feeling, keyword));
			});
			
			List<Query> queries = new ArrayList<>();			
			feelingKeywordsMap.forEach((feeling, keywords) -> groupKeywordsAndCreateQuery(queries, keywords, feeling));
			
			QueryDAO.saveQueries(queries);
			
		} catch (IOException e) {
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
		StringBuilder queryKeywords = new StringBuilder();
		String keywordSeparator = " " + Constants.KEYWORDS_LOGICAL_OPERATOR + " ";
		for(String keyword : keywords ) {
			if(counter >= Constants.KEYWORDS_GROUP_COUNT) {
				if(newQuery != null) {
					queries.add(newQuery.keywords(queryKeywords.toString()));
				}
				newQuery = new Query()
						.count(Constants.COUNT)
						.resultType(Constants.RESULT_TYPE)
						.lang(Constants.LANG)
						.filters(Constants.FILTERS)
						.geocode(Constants.GEO)
						.feeling(feeling);
				queryKeywords = new StringBuilder();
				counter = 0;
			}
			queryKeywords.append(counter == 0 ? "" : keywordSeparator).append(keyword);
			counter++;
		}
		if(newQuery != null) {
			queries.add(newQuery.keywords(queryKeywords.toString()));
		}
	}	

	private static void addToMap(Map<Feeling, List<String>> feelingKeywordsMap, Feeling feeling, String keyword) {
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
			feelingKeywordsMap.put(feeling, new ArrayList<>());
		}
		return feelingKeywordsMap;
	}
	
}

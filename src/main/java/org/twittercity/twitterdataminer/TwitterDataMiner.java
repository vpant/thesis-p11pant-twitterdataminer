package org.twittercity.twitterdataminer;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.init.DBMigration;
import org.twittercity.twitterdataminer.twitter.Twitter;
import org.twittercity.twitterdataminer.twitter.models.Feeling;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class TwitterDataMiner {
    private static Logger logger = LoggerFactory.getLogger(TwitterDataMiner.class);

    private static Random rand = new Random();
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

//        fixEmotionJsonFile();


    	try {
			DBMigration.buildTables();
		} catch (TwitterException e) {
			logger.error(e.getMessage());
			return;
		}

		Twitter.searchAndSave();

		logger.info("Program Finished!");
    }

    private static void fixEmotionJsonFile() throws IOException {
        URL emotionsJson = TwitterDataMiner.class.getClassLoader().getResource("nrc_en.json");

        Map<String, List<String>> keywordByEmotions = objectMapper.readValue(emotionsJson, Map.class);

        Map<Feeling, List<String>> feelingByKeyword = initializeKeywordsMap();

        keywordByEmotions.forEach((keyword, emotion) -> {
            Feeling feeling = Feeling.getByName(findValidEmotion(emotion));

            if (!(feeling == Feeling.NO_FEELING)) {
                feelingByKeyword.get(feeling).add(keyword);
            }
        });

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("target/keywords.json"), feelingByKeyword);
    }

    private static String findValidEmotion(List<String> emotions) {
        List<String> listToCheckAgainst = Arrays.asList("joy", "sadness", "fear", "anger", "disgust");

        List<String> validEmotions = emotions.stream().filter(listToCheckAgainst::contains).collect(Collectors.toList());

        return !validEmotions.isEmpty() ? validEmotions.get(rand.nextInt(validEmotions.size())) : "";
    }

    private static Map<Feeling, List<String>> initializeKeywordsMap() {
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

package org.twittercity.twitterdataminer.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.http.HttpRequest;
import org.twittercity.twitterdataminer.http.HttpResponse;
import org.twittercity.twitterdataminer.http.HttpResponseCode;
import org.twittercity.twitterdataminer.json.ParseUtil;

public class OAuth2 
{
	private static Logger logger = LoggerFactory.getLogger(OAuth2.class);
	
    private final static String OAUTH2_URL = "https://api.twitter.com/oauth2/token";
    private static OAuth2ConfigManager config = OAuth2ConfigManager.getInstance();

    
    public static String getBearerToken() throws  TwitterException
    {

        String bearerToken;
		
        bearerToken = config.getBearerToken();
        if ( bearerToken == null || bearerToken.trim().isEmpty())
        {
            try {
				bearerToken = getNewBearerToken();
			} catch (TwitterException te) {
				if(te.getResponseCode() == HttpResponseCode.FORBIDDEN){
					throw new TwitterException("Could not get access_token because authorization failed please check if Consumer Secret and Key are valid.");
				}
				throw te;
			}
        }
        return bearerToken;
    }
    
    
    public static String getNewBearerToken() throws TwitterException
    {
        
        String consumerSecret = null;
        String consumerKey = null;
	
		if ((consumerSecret = config.getConsumerSecret()) == null || (consumerKey = config.getConsumerKey()) == null) {
			throw new TwitterException("Open the file with name " + config.getOauth2ConfigName()
					+ ", that you can find at your jars folder "
					+ "(if it is not there create it), with a text editor and add in seperate"
					+ " lines in this format:\nconsumer_key=(And the consumer key you get by creating an app at https://apps.twitter.com )"
					+ "\nconsumer_secret=(And the consumer key you get by creating an app at https://apps.twitter.com )");
		}
        
        String bearerToken = "";                   
		
        HttpRequest httpRequest = buildRequest(consumerKey, consumerSecret);
		HttpResponse httpResponse;
		try {
			httpResponse = httpRequest.handleRequest();
			logger.info("Twitter requested. The response code is: {}", httpResponse.getResponseCode());
			bearerToken = ParseUtil.getString("access_token", httpResponse.asJSONObject());
			logger.debug("bearer_token: {}", bearerToken);
			OAuth2ConfigManager.getInstance().saveBearerToken(bearerToken);
		} catch (TwitterException te) {
			throw te;
		}
        return bearerToken;
    }
    
    
    private static HttpRequest buildRequest(String consumerKey, String consumerSecret) throws TwitterException
    {                  
		
		try {
			// Encode the credentials
			consumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			consumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new TwitterException("UnsupportedEncodingExcpetion while trying to encode consumer key and secret.");
		}

		String forTheBearerToken = consumerKey + ":" + consumerSecret;
		// Base64 encoding
		String bearerTokenBase64Encoded = new String(Base64.getEncoder().encode(forTheBearerToken.getBytes()));
		
		Map<String,String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Host", "api.twitter.com");
        requestHeaders.put("User-Agent", "twittercity.org Application / mailto:administration@twittercity.org");
        requestHeaders.put("Authorization", "Basic " + bearerTokenBase64Encoded);
        requestHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
        String postData = "grant_type=client_credentials";
		
        logger.info("Request parameters builded. Requesting twitter for access_token.");
		
        HttpRequest httpRequest = new HttpRequest(OAUTH2_URL, "POST", requestHeaders, postData);
        
        return httpRequest;
    }
}

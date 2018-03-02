/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twittercity.twitterdataminer.oauth2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterDataMiner;
import org.twittercity.twitterdataminer.TwitterException;

/**
 *
 * @author Bill
 */
public class OAuth2ConfigManager 
{
	private Logger logger = LoggerFactory.getLogger(OAuth2ConfigManager.class);
	
    private final String CONSUMERVALUESCONFIGNAME = "oauth.properties";
    private final String TWITTERSETCONFIG = "twittersett.properties";
    private static OAuth2ConfigManager instance = null;
       
    private OAuth2ConfigManager(){ /* nothing to initialize */ }
    
    public static OAuth2ConfigManager getInstance()
    {
        if (instance == null)
        {
            instance = new OAuth2ConfigManager();
        }
        return instance;
    }
    
    
    public String getConsumerKey() throws TwitterException 
    {
    	String consumerKey;
    	consumerKey = getPropertyValueOrEmpty(getConfigPath(CONSUMERVALUESCONFIGNAME),"consumer_key");
    	return consumerKey;     
    }
    

    public String getConsumerSecret() throws TwitterException
    {
    	return getPropertyValueOrEmpty(getConfigPath(CONSUMERVALUESCONFIGNAME),"consumer_secret");
    }
       
    public String getBearerToken() throws TwitterException 
    {
    	return getPropertyValueOrEmpty(getConfigPath(TWITTERSETCONFIG), "bearer_token");
    }

    public String getCachedUrl() throws TwitterException  
    {
    	return getPropertyValueOrEmpty(getConfigPath(TWITTERSETCONFIG), "cached_url");
    }
    
    public String getOauth2ConfigName()
    {
    	return CONSUMERVALUESCONFIGNAME;
    }
    
    //Bearer token and cachedUrl are in the same file. In order to update one of the their values we need to resave the value that will not change.
    public void saveBearerToken(String bearerToken) throws TwitterException 
    {
    	saveTwitterSettingsFile(bearerToken, getPropertyValueOrEmpty(getConfigPath(TWITTERSETCONFIG), "cached_url"));
    }
    
    public void saveCachedUrl(String cachedUrl) throws TwitterException
    {
    	saveTwitterSettingsFile(getPropertyValueOrEmpty(getConfigPath(TWITTERSETCONFIG), "bearer_token"), cachedUrl);
    }
    
    private void saveTwitterSettingsFile(String bearerToken, String cachedUrl)
    {
        Properties prop = new Properties();
        OutputStream output = null;
        try 
        {
            output = new FileOutputStream(getConfigPath(TWITTERSETCONFIG));

            prop.setProperty("bearer_token", bearerToken);
            prop.setProperty("cached_url", cachedUrl);
            // save properties to project root folder
            prop.store(output, null);
        } 
        catch (IOException io) 
        {
        	logger.error("IOException occured trying to save bearer token.");
        } 
        finally 
        {
            if (output != null) 
            {
                try{ output.close(); } 
                catch (IOException ioe){ /*nothing to do */}
            }
        }
    }
    
    
    // Get the consumer key or secret value
    private String getPropertyValueOrEmpty(String path, String key) throws TwitterException
    {	
    	Properties prop = new Properties();
    	String value = "";
        try 
        {   
            // load a properties file
            prop.load(new FileInputStream(path));
            value = prop.getProperty(key);
        } 
        catch (FileNotFoundException fnfe)
        {
        	try
        	{
        		logger.warn("File in {} not found, trying to create one.", path);
        		createConfFile(path);
        	}
        	catch (IOException ioe)
        	{
        		throw new TwitterException("Failed to create the file, it should be created manually!");
        	}
        	
        } catch (IOException e) {
			logger.error("Property file could not be loaded so we cannot read the value for the key {} and we return null instead.", key);	
			value = "";
		}
        
        if ( value == null || value.isEmpty())
        {
            value = "";
        }
        return value;
    }

    
    //Get jar's location path
    private String getConfigPath(String config)
    {
        String path = null;
        CodeSource src = TwitterDataMiner.class.getProtectionDomain().getCodeSource();
        if (src != null) 
        {
            try 
            {
                URL url = new URL(src.getLocation(), config);
                path = url.getPath();
            }
            catch (MalformedURLException mue) 
            {
            	logger.error("Malformed URL exception while trying to create config path.");
            }
        }
        return path;
    }
    
    
    //Create empty configuration file
    private static void createConfFile(String path) throws IOException
    {
        File newConfig = new File(path);
        //if file already exists will do nothing
        newConfig.createNewFile();
    }         
}
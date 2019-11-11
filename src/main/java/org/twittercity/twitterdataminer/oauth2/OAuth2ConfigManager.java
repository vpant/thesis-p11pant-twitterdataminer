/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twittercity.twitterdataminer.oauth2;

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
public class OAuth2ConfigManager {
	private Logger logger = LoggerFactory.getLogger(OAuth2ConfigManager.class);
	private static OAuth2ConfigManager instance = null;

	private OAuth2ConfigManager() {
		/* nothing to initialize */ }

	public static OAuth2ConfigManager getInstance() {
		if (instance == null) {
			instance = new OAuth2ConfigManager();
		}
		return instance;
	}

	public String getConsumerKey() throws TwitterException {
		return getPropertyValueOrEmpty(ConfigType.OAUTH_TOKENS, "consumer_key");
	}

	public String getConsumerSecret() throws TwitterException {
		return getPropertyValueOrEmpty(ConfigType.OAUTH_TOKENS, "consumer_secret");
	}

	public String getBearerToken() throws TwitterException {
		return getPropertyValueOrEmpty(ConfigType.SETTINGS, "bearer_token");
	}

	public String getCachedUrl() throws TwitterException {
		return getPropertyValueOrEmpty(ConfigType.SETTINGS, "cached_url");
	}

	public String getOauth2ConfigName() {
		return ConfigType.OAUTH_TOKENS.getFileName();
	}

	// Bearer token and cachedUrl are in the same file. In order to update one of
	// the their values we need to re-save the value that will not change.
	public void saveBearerToken(String bearerToken) throws TwitterException {
		saveTwitterSettingsFile(bearerToken, "");
	}

	public void saveCachedUrl(String cachedUrl) throws TwitterException {
		saveTwitterSettingsFile("", cachedUrl);
	}

	private void saveTwitterSettingsFile(String bearerToken, String cachedUrl) {
		Properties prop = new Properties();
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			String path = getConfigPath(ConfigType.SETTINGS.getFileName());
			input = new FileInputStream(path);
			prop.loadFromXML(input);
			input.close();
			
			output = new FileOutputStream(path);
			
			if (bearerToken != null && bearerToken != "") {
				prop.setProperty("bearer_token", bearerToken);
			}
			if (cachedUrl != null && cachedUrl != "") {
				prop.setProperty("cached_url", cachedUrl);
			}
			prop.storeToXML(output, "Used from the data mining application to request Twitter.");
		} catch (IOException io) {
			logger.error("IOException occured trying to save bearer token.");
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException ioe) {
					/* nothing to do */}
			}
		}
	}

	// Get the consumer key or secret value
	private String getPropertyValueOrEmpty(ConfigType confingType, String key) throws TwitterException {
		Properties prop = new Properties();
		String value = "";
		String path = getConfigPath(confingType.getFileName());
		try {
			// load a properties file
			prop.loadFromXML(new FileInputStream(path));
			value = prop.getProperty(key);
			if (value != null) {
				value.trim();
			}
		} catch (FileNotFoundException fnfe) {
			try {
				logger.warn("File in {} not found, trying to create one.", path);
				createConfFile(confingType, path);
			} catch (IOException ioe) {
				throw new TwitterException("Failed to create the file, it should be created manually!");
			}

		} catch (IOException e) {
			logger.error(
					"Property file could not be loaded so we cannot read the value for the key {} and we return null instead.",
					key);
			value = "";
		}

		if (value == null || value.isEmpty()) {
			value = "";
		}
		return value;
	}

	// Get jar's location path
	private String getConfigPath(String config) {
		String path = null;
		CodeSource src = TwitterDataMiner.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			try {
				URL url = new URL(src.getLocation(), config);
				path = url.getPath();
			} catch (MalformedURLException mue) {
				logger.error("Malformed URL exception while trying to create config path.");
			}
		}
		return path;
	}

	private void createConfFile(ConfigType confType, String path) throws IOException {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(getConfigPath(path));
			
			if(confType == ConfigType.OAUTH_TOKENS) {
				prop.setProperty("consumer_key", " ");
				prop.setProperty("consumer_secret", " ");
			}
			else {
				prop.setProperty("bearer_token", " ");
				prop.setProperty("cached_url", " ");
			}

			// save properties to project root folder
			prop.storeToXML(output,
					"Fill consumer_key and consumer_secret with the respective fields from Twitter Apps");
		} catch (IOException io) {
			logger.error("IOException occured trying to create OAuth file.");
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException ioe) {
					/* nothing to do */}
			}
		}
	}

	private enum ConfigType {
		SETTINGS("twitter_settings.xml"), OAUTH_TOKENS("oauth.xml");
		
		private String fileName;
		
		private ConfigType(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return fileName;
		}
	}
}
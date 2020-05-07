/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twittercity.twitterdataminer.oauth2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twittercity.twitterdataminer.TwitterDataMiner;
import org.twittercity.twitterdataminer.TwitterException;
import org.twittercity.twitterdataminer.utilities.ClassLoaderUtil;

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


	public String getOauth2ConfigName() {
		return ConfigType.OAUTH_TOKENS.getFileName();
	}


	@Deprecated
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

	// Get the consumer key or secret value
	private String getPropertyValueOrEmpty(ConfigType confingType, String key) throws TwitterException {
		Properties prop = new Properties();
		String value = "";
		try {
			// load a properties file
			InputStream stream = ClassLoaderUtil.getResourceAsStream(confingType.getFileName(),
					OAuth2ConfigManager.class);
			if (stream == null) {
				logger.warn("{} config file not found!", confingType.getName());
				value = "";
			} else {
				prop.loadFromXML(stream);
				value = prop.getProperty(key);
				if (value != null) {
					value = value.trim();
				}
			}

		} catch (IOException e) {
			// Do nothing
		}

		if (value == null || value.isEmpty()) {
			value = "";
		}
		return value;
	}

	private enum ConfigType {
		SETTINGS("twitter_settings.xml", "Settings"), OAUTH_TOKENS("oauth.xml", "OAuth");

		private String fileName;
		private String name;

		private ConfigType(String fileName, String name) {
			this.fileName = fileName;
			this.name = name;
		}

		public String getFileName() {
			return fileName;
		}

		public String getName() {
			return name;
		}
	}
}
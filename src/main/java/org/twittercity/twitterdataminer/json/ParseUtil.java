package org.twittercity.twitterdataminer.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;

public class ParseUtil {

	private ParseUtil(){}
	
	
	public static String getString (String key, JSONObject jsonObject)
    {
        if (jsonObject.isNull(key)){
        	return null;
        }
        else{
        	return jsonObject.getString(key);
        }
		
    }
	

	public static boolean getBoolean(String key, JSONObject json) {

		return json.getBoolean(key);
	}
	
    public static String getDate(String key, JSONObject json) throws TwitterException  {
        String dateStr = getString(key, json);
        String format = "EEE MMM dd HH:mm:ss z yyyy";
        if ("null".equals(dateStr) || null == dateStr) {
            return null;
        } else {
            return getDate(dateStr, format);
        }
    }
    
    public static String getDate(String dateStr, String format) throws TwitterException {
    	SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);	
    	sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    	Date date;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException pe) {
			throw new TwitterException("Could not parse date to a date object.Message: " + pe.getMessage());
		}
    	   	
    	return new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US).format(date);
    }
}

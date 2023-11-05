package org.twittercity.twitterdataminer.utilities.json;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.TwitterException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ParseUtil {

    private ParseUtil() {
    }


    public static String getString(String key, JSONObject jsonObject) {
        return jsonObject.isNull(key) ? null : jsonObject.getString(key);
    }

    public static int getInt(String key, JSONObject jsonObject) {
        return jsonObject.getInt(key);
    }

    public static boolean getBoolean(String key, JSONObject json) {
        return json.getBoolean(key);
    }

    public static String getDate(String key, JSONObject json) throws TwitterException {
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
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException pe) {
            throw new TwitterException("Could not parse date to a date object.Message: " + pe.getMessage());
        }

        return new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US).format(date);
    }

    public static JSONObject getJSON(String key, JSONObject json) {
        if (json.isNull(key)) {
            return null;
        } else {
            return json.getJSONObject(key);
        }
    }
}

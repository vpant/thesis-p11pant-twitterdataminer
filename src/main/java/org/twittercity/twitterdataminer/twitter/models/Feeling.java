package org.twittercity.twitterdataminer.twitter.models;

import java.awt.Color;

public enum
Feeling {
	// Sync color values with TwitterCityDataMiner#Feeling enum
	JOY(0, "Joy", new Color(255, 255, 0).getRGB()), 
	SADNESS(1, "Sadness", new Color(128, 128, 128).getRGB()), 
	FEAR(2, "Fear", new Color(0, 0, 0).getRGB()),
	ANGER(3, "Anger", new Color(255, 0, 0).getRGB()),
	DISGUST(4, "Disgust", new Color(144, 238, 144).getRGB()),
	NO_FEELING(-1, "", -1);
	
	private final int value;
	private final String feelingName;
	private final int colorCode;
	
	Feeling(int value, String name, int colorCode) {
        this.value = value;
        this.feelingName = name;
        this.colorCode = colorCode;
    }
	
	public int getFeelingID() {
		return value;
	}

	public int getFeelingColor() {
		return colorCode;
	}
	
	public String getFeelingName() {
		return feelingName;
	}
	
	public static Feeling forFeelingID(int feelingID) {
		for (Feeling feeling: values()) {
            if (feeling.getFeelingID() == feelingID) {
            	return feeling;
            }          
        }
		return NO_FEELING;
	}
	
	public static Feeling getByName(String name) {
		for (Feeling feeling : values()) {
            if (feeling.getFeelingName().equalsIgnoreCase(name)) {
            	return feeling;
            }          
        }
		return NO_FEELING;
	}


} 


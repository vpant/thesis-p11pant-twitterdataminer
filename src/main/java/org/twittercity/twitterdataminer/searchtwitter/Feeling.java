package org.twittercity.twitterdataminer.searchtwitter;

import java.awt.Color;

public enum Feeling {
	// Sync color values with TwitterCityDataMiner#Feeling enum
	HAPPY(0, new Color(255,0,0).getRGB()), SAD(1, new Color(0,255,0).getRGB()), NO_FEELING(-1, -1);
	
	private int value;
	private int colorCode;
	
	private Feeling(int value, int colorCode) {
        this.value = value;
        this.colorCode = colorCode;
    }
	
	public int getFeelingID() {
		return value;
	}
	
	public int getFeelingColor() {
		return colorCode;
	}
	
	public static Feeling forFeelingID(int feelingID) {
		for (Feeling feeling: values()) {
            if (feeling.getFeelingID() == feelingID) {
            	return feeling;
            }          
        }
		return NO_FEELING;
	}
} 


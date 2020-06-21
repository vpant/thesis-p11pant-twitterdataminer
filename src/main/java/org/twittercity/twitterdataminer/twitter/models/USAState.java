package org.twittercity.twitterdataminer.twitter.models;

public enum USAState {
	ALABAMA(1, "Alabama", "AL"),
    ALASKA(2, "Alaska", "AK"),
    ARIZONA(3, "Arizona","AZ"),
    ARKANSAS(4, "Arkansas", "AR"),
    CALIFORNIA(5, "California", "CA"),
    COLORADO(6, "Colorado", "CO"),
    CONNECTICUT(7, "Connecticut", "CT"),
    DELAWARE(8, "Delaware", "DE"),
    DISTRICT_OF_COLUMBIA(9, "District of Columbia", "DC"), // Technically not a state
    FLORIDA(10, "Florida", "FL"),
    GEORGIA(11, "Georgia", "GA"),
    HAWAII(12, "Hawaii", "HI"),
    IDAHO(13, "Idaho", "ID"),
    ILLINOIS(14, "Illinois", "IL"),
    INDIANA(15, "Indiana", "IN"),
    IOWA(16, "Iowa", "IA"),
    KANSAS(17, "Kansas", "KS"),
    KENTUCKY(18, "Kentucky", "KY"),
    LOUISIANA(19, "Louisiana", "LA"),
    MAINE(20, "Maine", "ME"),
    MARYLAND(21, "Maryland", "MD"),
    MASSACHUSETTS(22, "Massachusetts", "MA"),
    MICHIGAN(23, "Michigan", "MI"),
    MINNESOTA(24, "Minnesota", "MN"),
    MISSISSIPPI(25, "Mississippi", "MS"),
    MISSOURI(26, "Missouri", "MO"),
    MONTANA(27, "Montana", "MT"),
    NEBRASKA(28, "Nebraska", "NE"),
    NEVADA(29, "Nevada", "NV"),
    NEW_HAMPSHIRE(30, "New Hampshire", "NH"),
    NEW_JERSEY(31, "New Jersey", "NJ"),
    NEW_MEXICO(32, "New Mexico", "NM"),
    NEW_YORK(33, "New York", "NY"),
    NORTH_CAROLINA(34, "North Carolina", "NC"),
    NORTH_DAKOTA(35, "North Dakota", "ND"),
    OHIO(36, "Ohio", "OH"),
    OKLAHOMA(37, "Oklahoma", "OK"),
    OREGON(38, "Oregon", "OR"),
    PENNSYLVANIA(39, "Pennsylvania", "PA"),
    RHODE_ISLAND(40, "Rhode Island", "RI"),
    SOUTH_CAROLINA(41, "South Carolina", "SC"),
    SOUTH_DAKOTA(42, "South Dakota", "SD"),
    TENNESSEE(43, "Tennessee", "TN"),
    TEXAS(44, "Texas", "TX"),
    UTAH(45, "Utah", "UT"),
    VERMONT(46, "Vermont", "VT"),
    VIRGINIA(47, "Virginia", "VA"),
    WASHINGTON(48, "Washington", "WA"),
    WEST_VIRGINIA(49, "West Virginia", "WV"),
    WINSCONSIN(50, "Wisconsin", "WI"),
    WYOMING(51, "Wyoming", "WY");
    	
	private int id;
    private String name;
	private String abbrv;


	private USAState(int id, String name, String abbrv) {
		this.id = id;
		this.name = name;
		this.abbrv = abbrv;
	}
    
	int getID() {
		return id;
	}
	
	String getName() {
		return name;
	}

    String getAbbrv() {
		return abbrv;
	}

	public static USAState getStateByAbbrv(String abbrv) {
		for (USAState state: values()) {
            if (state.getAbbrv().equalsIgnoreCase(abbrv)) {
            	return state;
            }          
        }
		return null;
	}
	
	public static USAState getStateByName(String name) {
		for (USAState state: values()) {
            if (state.getName().equalsIgnoreCase(name)) {
            	return state;
            }          
        }
		return null;
	}
}
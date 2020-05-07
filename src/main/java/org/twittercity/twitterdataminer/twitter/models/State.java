package org.twittercity.twitterdataminer.twitter.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.utilities.json.ParseUtil;

import com.sun.istack.Nullable;

@Entity
@Table(name = "usa_state")
public class State {

	@Id
	@Column(name = "id")
	private int dbId;
	@Column(name = "state_name")
	private String stateName;
	@Column(name = "state_abbr")
	private String stateAbbr;
	
	
	private State(USAState state) {
		if(state != null) {
			this.dbId = state.getID();
			this.stateName = state.getName();
			this.stateAbbr = state.getAbbrv();
		}
		else {
			this.dbId = 0;
			this.stateName = "";
			this.stateAbbr = "";
		}
	}
	
	
	@Nullable
	public static State fromJson(JSONObject json) {
		if(json == null) {
			return null;
		}
		State state = null;
		JSONObject placeJSON = ParseUtil.getJSON("place", json);
		if(placeJSON != null ) {
			if(!"United States".equals(ParseUtil.getString("country", placeJSON))) {
				
				if("admin".equals(ParseUtil.getString("place_type", placeJSON))) {
					
					state = new State(USAState.getStateByName(ParseUtil.getString("name", placeJSON)));
				}
				else if("city".equals(ParseUtil.getString("place_type", placeJSON))) {
					
					String placeName = ParseUtil.getString("name", placeJSON);
					USAState stateEnum = USAState.getStateByName(placeName.substring(placeName.lastIndexOf(", ") + 1));
					state = (stateEnum == null) ? null : new State(stateEnum);
				}
			}
		}
		else {
			String placeName = ParseUtil.getString("location", ParseUtil.getJSON("user", json));
			if(placeName == null) {
				state = null;
			}
			else {
				USAState stateEnum = USAState.getStateByName(placeName.substring(placeName.lastIndexOf(", ") + 1));
				state = (stateEnum == null) ? null : new State(stateEnum);
			}
		}
		return state;
	}
	
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
        
		private int getID() {
			return id;
		}
		
		private String getName() {
			return name;
		}

        private String getAbbrv() {
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

}

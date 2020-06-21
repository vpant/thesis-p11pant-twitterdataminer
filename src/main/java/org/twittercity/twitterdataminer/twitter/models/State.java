package org.twittercity.twitterdataminer.twitter.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;
import org.twittercity.twitterdataminer.database.dao.StateDAO;
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
	
	public State() { }
	
	public State(USAState state) {
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
					USAState stateEnum = USAState.getStateByName(ParseUtil.getString("name", placeJSON));
					if(stateEnum != null) {
						state = StateDAO.getStateById(stateEnum.getID());
						if(state == null) {
							state = new State(stateEnum);
						}
					}
				}
				else if("city".equals(ParseUtil.getString("place_type", placeJSON))) {
					
					String placeName = ParseUtil.getString("name", placeJSON);
					USAState stateEnum = USAState.getStateByName(placeName.substring(placeName.lastIndexOf(", ") + 1));
					if(stateEnum != null) {
						state = StateDAO.getStateById(stateEnum.getID());
						if(state == null) {
							state = new State(stateEnum);
						}
					}
				}
			}
		}
		else {
			String placeName = ParseUtil.getString("location", ParseUtil.getJSON("user", json));
			if(placeName != null) {
				USAState stateEnum = USAState.getStateByName(placeName.substring(placeName.lastIndexOf(", ") + 1));
				if(stateEnum != null) {
					state = StateDAO.getStateById(stateEnum.getID());
					if(state == null) {
						state = new State(stateEnum);
					}
				}
			}
		}
		return state;
	}

}

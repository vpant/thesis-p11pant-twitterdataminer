package org.twittercity.twitterdataminer.init;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "application_state")
public class ApplicationStateData {
	
	@Id
	@Column(name = "id")
	private int dbId;
	@Column(name = "bearer_token")
	private String bearer;
	@Column(name = "last_searched_query_id")
	private int lastSearchedQueryId;
	
	public ApplicationStateData() {
		dbId = 0;
		bearer = "";
		lastSearchedQueryId = 1;
	}
	
	public int getLastSearchedQueryId() {
		return lastSearchedQueryId;
	}

	public void setLastSearchedQueryId(int lastSearchedQueryId) {
		this.dbId = 0;
		this.lastSearchedQueryId = lastSearchedQueryId;
	}

	public String getBearerToken() {
		return bearer;
	}

	public void setBearerToken(String bearerToken) {
		this.dbId = 0;
		this.bearer = bearerToken;
	}
	
}

package org.b3mn.poem.security;

import java.util.Date;

public class AuthenticationToken {

	private String authToken;
	
	private String userUniqueIdentifier;
	
	private Date creationDate;
	
	private Date lastRequest;
	
	public AuthenticationToken(String authToken, String userUniqueId) throws AuthenticationTokenException {
		if(authToken == null || authToken == "") {
			throw new AuthenticationTokenException("AuthToken is null or empty.");
		}
		if(userUniqueId == null || userUniqueId == "") {
			throw new AuthenticationTokenException("User's unique id is null or empty.");
		}
		this.authToken = authToken;
		this.userUniqueIdentifier = userUniqueId;
		
		this.creationDate = new Date();
		this.lastRequest = new Date();
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public String getUserUniqueId() {
		return userUniqueIdentifier;
	}
	
	public String toString() {
		return userUniqueIdentifier + " " + authToken;
	}
	
	public Date getCreationDate() {
		return (Date) this.creationDate.clone();
	}
	
	public Date getLastRequestDate() {
		return (Date) this.lastRequest.clone();
	}
	
	public void setLastRequestDate(Date lastRequest) {
		this.lastRequest = (Date) lastRequest.clone();
	}
}

package pt.unl.fct.di.apdc.webapp.util;

import java.util.UUID;

public class AuthToken {

	public String username;
	public String tokenId;
	public long creationDate;
	public long expirationDate;
	public String role;
	
	public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2h
	
	public AuthToken(String username) {
		this.username = username;
		this.tokenId = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + AuthToken.EXPIRATION_TIME;
		this.role = null;
	}
	
	public AuthToken(String username, String tokenId, long creationDate, long expirationDate, String role) {
		this.username = username;
		this.tokenId = tokenId;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.role = role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
}
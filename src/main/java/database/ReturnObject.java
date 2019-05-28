package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnObject {
	private boolean test;
	private String message;
	private ArrayList<String> warning = new ArrayList<String>();
	private Optional<Long> sessionID = Optional.ofNullable(null);
	private String token ;

	public ArrayList<String> getWarning() {
		return warning;
	}

	public void addWarning(String warning) {
		this.warning.add(warning);
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Optional<Long> getSessionID() {
		return sessionID;
	}

	public void setSessionID(Optional<Long> sessionID) {
		this.sessionID = sessionID;
	}
	
	public void setSessionID(long sessionID) {
		this.sessionID = Optional.of(sessionID);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setWarning(ArrayList<String> warning) {
		this.warning = warning;
	}
	

}

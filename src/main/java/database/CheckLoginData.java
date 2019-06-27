package database;

import java.nio.CharBuffer;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import database.connection.ConnectionToDB;
import database.connection.ShouldBeOnlyOneException;
import database.hash.Hash;
import database.hash.Password;
import Server.ServerGUI;

public class CheckLoginData {
	private Hash hash = new Hash();
	private ConnectionToDB conn = null;

	public CheckLoginData(ConnectionToDB conn) {
		this.conn = conn;
	}

	String charToString(char[] pw) {
		StringBuilder temp = new StringBuilder();
		for (char c : pw) {
			temp.append(c);			
		}
		ServerGUI.print("charToString() = " + temp);
		// TODO delete this
		return temp.toString();
	}

	private String returnUsername(long sessionID) throws SQLException {

//		ServerGUI.print("SELECT `ID` FROM `SessionID` WHERE `Email` ='" + sessionID + "'");
		String ID = conn.readStringDB("SELECT `ID` FROM `SessionID` WHERE `Email` ='" + sessionID + "'").get(0);
		// serverGUI.print("SELECT `email` FROM `users` WHERE `Email` ='" + ID + "'");
		String email = conn.readStringDB("SELECT `email` FROM `users` WHERE `Email` ='" + ID + "'").get(0);

		return email;
	}

	public ReturnObject insertNewPW(String username, char[] pw) throws SQLException {
		ServerGUI.print("in insertnewPW( " + username + " , " + charToString(pw)+ " )");
		var pw2 = duplicatePW(pw);
		Hash hash = new Hash();

		String[] fields = { "password", "passwordSalt", "passwordHashAlgorithm" };
		Password hashedPasswordData = hash.newPW(username, pw);
		byte[] salt = hashedPasswordData.getSalt();
		StringBuilder temp = new StringBuilder();
		for (byte b : salt) {
			temp.append(b + ",");
		}

		String[] data = { hashedPasswordData.getHashedPassword(), temp.toString(),
				hashedPasswordData.getHashedAlgorithm() };
		conn.updateDB("users", fields, data, "`Email`= '" + username + "'");
//		("users", fields, data);

		return checkPassword("", username, pw2);

	}

	public ReturnObject check(char... pw) {
		ReturnObject returnObject = new ReturnObject();
		try {
			returnObject = checkToken(pw);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated catch block

		return returnObject;
	}

	public ReturnObject checkToken(char... token) throws SQLException {
		ReturnObject returnObject = new ReturnObject();
		LocalDate date = LocalDate.now();
		date = date.minusMonths(1);
		StringBuilder query = new StringBuilder();
		query.append("SELECT `ID` FROM `Session ID` WHERE `token` = '");
		query.append(charToString(token));
		query.append("' AND `date created` >= '" + date + "'");

		// serverGUI.print(query.toString());
		List<Long> temp = null;
		try {
			temp = conn.readLongDB(query.toString());

		} catch (IndexOutOfBoundsException IOOB) {
			// serverGUI.print("no valid token");
//			ServerGUI.print("Optional sessionID is " + sessionID.isPresent());
		}

		if (temp.size() == 1) {
			returnObject.setSessionID(Optional.of(temp.get(0)));
		} else {
			throw new ShouldBeOnlyOneException("Too much data returned");
		}
		return returnObject;
	}

	public boolean checkPassword(String ipAddress, long sessionID, char... pw) throws SQLException {
		// get username from sessionID
		ServerGUI.print("in chechkPassword(" + ipAddress + " , " + sessionID + " , " + charToString(pw) + ")");
		String email = returnUsername(sessionID);

		// check PW

		ReturnObject temp = checkPassword(ipAddress, email, pw);
		// return true if PW is correct

		return temp.getMessage().equals("Welcome");
	}

	public ReturnObject checkPassword(String ipAddress, String user, char... pw) throws SQLException {
		ServerGUI.print(
				"in checkPassword( " + ipAddress + " , " + user + " , " + charToString(pw) + ") ");
		ReturnObject returnObject = new ReturnObject();
		Optional<Long> sessionID = Optional.ofNullable(null);

//		ServerGUI.print("SELECT `passwordHashAlgorithm` FROM `users` WHERE `Email` ='" + user + "'");
		String algorithm = conn
				.readStringDB("SELECT `passwordHashAlgorithm` FROM `users` WHERE `Email` ='" + user + "'").get(0);
		ServerGUI.print("algorithm is " + algorithm);
		String hashedPW = conn.readStringDB("SELECT `password` FROM `users` WHERE `Email` ='" + user + "'").get(0);
		ServerGUI.print("hashedPW is " + hashedPW);
//		ServerGUI.print("SELECT `password` FROM `users` WHERE `Email` ='" + user + "'");
		String saltString = conn.readStringDB("SELECT `passwordSalt` FROM `users` WHERE `Email` ='" + user + "'")
				.get(0);
//		ServerGUI.print("SELECT `passwordSalt` FROM `users` WHERE `Email` ='" + user + "'");
		ServerGUI.print("saltString is " + saltString);
		int userID = conn.readIntDB("SELECT `user ID` FROM `users` WHERE `Email` ='" + user + "'").get(0);
		ServerGUI.print("userID is " + userID);

		sessionID = hash.checkPW(saltString, pw, hashedPW, algorithm);

		List<String> oldHashMethods = List.of("none", "test");
		if (oldHashMethods.contains(algorithm)) {
			returnObject.addWarning("Old Hash Method");
		}

		String token = "";
		if (sessionID.isPresent()) {
			token = generateToken(sessionID.get(), userID, ipAddress);
			returnObject.setMessage("Welcome");
		}

		returnObject.setToken(token);

		returnObject.setSessionID(sessionID);

		return returnObject;
	}

	public String generateToken(Long sessionID, int userID, String ipAddress) throws SQLException {
		SecureRandom secureRandom = new SecureRandom();
		byte[] tokenByte = new byte[20];
		secureRandom.nextBytes(tokenByte);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenByte);

		String query = "INSERT INTO `Session ID`(`ID`, `user ID`, `last Login`, `token`, `date created`, `IP-adress`) VALUES ('"
				+ sessionID + "','" + userID + "','" + LocalDateTime.now() + "','" + token + "','" + LocalDateTime.now()
				+ "','" + ipAddress + "')";
		conn.createDB(query);

		return token;

	}

	public String getToken(long sessionID) {
		String queryBuilder = "SELECT `token` FROM `Session ID` WHERE `ID` ='" + sessionID + "'";
//		ServerGUI.print(queryBuilder);
		String query = queryBuilder;

		try {
			List<String> token = conn.readStringDB(query);
		} catch (SQLException e) {
			return "" + sessionID;
			// TODO Auto-generated catch block
		}

		return "";
	}

	public ReturnObject newUser(String user, char[] pw) throws SQLException {
		ServerGUI.print("in newUser( " + user + " , " + charToString(pw) + ")");
		String queryUsername = "SELECT `user ID` FROM `smtdb`.`users` WHERE `Email` ='" + user + "'";

		ReturnObject returnObject = new ReturnObject();

		List<Integer> ids = conn.readIntDB(queryUsername);

		if (ids.size() == 0) {
			conn.createDB("users", "`email`='" + user + "'");

			return insertNewPW(user, pw);
		} else {
			returnObject.setMessage("username allready taken");
		}
		return returnObject;
	}

	private char[] duplicatePW(char[] pw) {
		char[] temp = new char[pw.length];
		for (int i = 0; i < pw.length; i++) {
			temp[i] = pw[i];
		}

		return temp;
	}
}
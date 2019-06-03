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

public class CheckPW {
	private Hash hash = new Hash();
	protected ConnectionToDB conn = null;

	public CheckPW(ConnectionToDB conn) {
		this.conn = conn;
	}

	public ReturnObject changePW(char[] oldPW, char[] pw, long sessionID) {
		ReturnObject returnObject = new ReturnObject();

		try {
			// get username from sessionID
			Integer id;
			String query = "SELECT `user ID` FROM `Session ID` WHERE `Token` = '" + sessionID + "'";
			List<Integer> listID = conn.readIntDB(query);
			if (listID.size() == 1) {
				id = listID.get(0);
			} else {
				throw new ShouldBeOnlyOneException("Too much data returned");
			}

			query = "SELECT `Email` FROM `users` WHERE `user ID` = '" + id + "'";
			List<String> listEmail = conn.readStringDB(query);
			String user = "";

			if (listEmail.size() == 1) {
				user = listEmail.get(0);
			} else {
				throw new ShouldBeOnlyOneException("Too much data returned");
			}

			// check oldPW
			// insert new pw
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnObject;

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
		query.append(token);
//				Stream.of(token).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));
//		query.append("'");
		query.append("' AND `date created` >= '" + date + "'");

		ServerGUI.print(query.toString());
		List<Long> temp = null;
		try {
			temp = conn.readLongDB(query.toString());

		} catch (IndexOutOfBoundsException IOOB) {
			ServerGUI.print("no valid token");
//			ServerGUI.print("Optional sessionID is " + sessionID.isPresent());
		}

		if (temp.size() == 1) {
			returnObject.setSessionID(Optional.of(temp.get(0)));
		} else {
			throw new ShouldBeOnlyOneException("Too much data returned");
		}
		return returnObject;
	}

	public ReturnObject checkPassword(String ipAddress, String user, char... pw) throws SQLException {
		ReturnObject returnObject = new ReturnObject();
		Optional<Long> sessionID = Optional.ofNullable(null);

		ServerGUI.print("in chechkPassword()");
		ServerGUI.print("SELECT `passwordHashAlgorithm` FROM `users` WHERE `Email` ='" + user + "'");
		String algorithm = conn
				.readStringDB("SELECT `passwordHashAlgorithm` FROM `users` WHERE `Email` ='" + user + "'").get(0);
		ServerGUI.print("algorithm is " + algorithm);
		String hashedPW = conn.readStringDB("SELECT `password` FROM `users` WHERE `Email` ='" + user + "'").get(0);
		ServerGUI.print("hashedPW is " + hashedPW);
		ServerGUI.print("SELECT `password` FROM `users` WHERE `Email` ='" + user + "'");
		String saltString = conn.readStringDB("SELECT `passwordSalt` FROM `users` WHERE `Email` ='" + user + "'")
				.get(0);
		ServerGUI.print("SELECT `passwordSalt` FROM `users` WHERE `Email` ='" + user + "'");
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
		ServerGUI.print(queryBuilder);
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
		Optional<Long> SessionID = Optional.ofNullable(null);
		ServerGUI.print("in newUser()");
		String queryUsername = "SELECT `user ID` FROM `smtdb`.`users` WHERE `Email` ='" + user + "'";
		ServerGUI.print(queryUsername);
		ServerGUI.print("ResultSet.size() " + conn.readIntDB(queryUsername).size());
		ServerGUI.print("ResultSet.size() " + conn.readIntDB(queryUsername).size());

		ReturnObject returnObject = new ReturnObject();

		List<Integer> ids = conn.readIntDB(queryUsername);

		ServerGUI.print("printing list of users with " + user + " as email");
		for (int i : ids) {
			ServerGUI.print("" + i);
		}

		if (ids.size() == 0) {

			String[] fields = { "email", "password", "passwordSalt", "passwordHashAlgorithm" };
			Password hashedPasswordData = hash.newPW(user, pw);
			byte[] salt = hashedPasswordData.getSalt();
			StringBuilder temp = new StringBuilder();
			for (byte b : salt) {
				temp.append(b + ",");
			}

			String[] data = { user, hashedPasswordData.getHashedPassword(), temp.toString(),
					hashedPasswordData.getHashedAlgorithm() };
			conn.createDB("users", fields, data);

			return checkPassword("", user, pw);
		} else {
			returnObject.setMessage("username allready taken");
		}
		return returnObject;
	}
}
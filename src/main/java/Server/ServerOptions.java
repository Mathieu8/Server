package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import database.CheckLoginData;
import database.ReturnObject;
import database.SaveMethod;
import database.connection.ConnectionToDB;
import measurements.BasicMeasurements;

public class ServerOptions {
	final ConnectionToDB conn = ConnectionToDB.createConn();
	Warnings warnings = new Warnings();

	private char[] readArray(DataInputStream input) throws IOException {
		int length = input.readInt();
		char[] array = new char[length];

		String deleteThis = "";
		for (int i = 0; i < length; i++) {
			array[i] = input.readChar();
//					ServerGUI.print(""+c[i]);
			deleteThis += array[i];
		}
		ServerGUI.print("PW is " + deleteThis);

		return array;

	}

	Optional<Long> sendNewAccount(DataInputStream input, DataOutputStream output) throws IOException {
		ServerGUI.print("in checkPW()");
		ConnectionToDB conn = ConnectionToDB.createConn();
		CheckLoginData cpw = new CheckLoginData(conn);
		String username = input.readUTF();
		ServerGUI.print("username is " + username);

		char[] pw = readArray(input);

		char[] pw2 = readArray(input);

		boolean equal = Arrays.equals(pw, pw2);
		ServerGUI.print("pw equals pw2 is " + equal);
		if (!equal) {
			output.writeUTF("different pw's");
			return Optional.ofNullable(null);
		}

		// insert new user into DB
		Optional<Long> sessionID = Optional.ofNullable(null);

		// send back new token
		String token = null;
		try {
			ReturnObject returnObject = cpw.newUser(username, pw);
			warnings.warning(input, output, returnObject);

			String massage = returnObject.getMessage();
			sessionID = returnObject.getSessionID();
			ServerGUI.print("Massage is " + massage);
			ServerGUI.print("!sessionID.isPresent() " + !sessionID.isPresent());
//			ServerGUI.print("sessionID.get() " + sessionID.get());

			output.writeUTF(massage);
			if (massage.equals("Welcome")) {
				ServerGUI.print("token is " + returnObject.getToken());

				output.writeUTF(returnObject.getToken());
				
				return Optional.ofNullable(null);
//			} else {
//				ServerGUI.print("sessionID " + sessionID.get());
//
//				token = cpw.getToken(sessionID.get());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (sessionID.isPresent() && !token.isEmpty()) {
			output.writeUTF("Welcome");
			output.writeUTF(token);
		}

		// TODO Auto-generated method stub
		return sessionID;
	}

	Optional<Long> checkToken(DataInputStream input, DataOutputStream output) throws IOException {
		ServerGUI.print("In CheckToken");
		ConnectionToDB conn = ConnectionToDB.createConn();
		CheckLoginData cpw = new CheckLoginData(conn);
		var temp = "";
		char[] token = readArray(input);
		String date = "";
		try {
			date = input.readUTF();
		} catch (EOFException e) {
			// do nothing
			output.writeUTF("Wrong token");
			ServerGUI.print("sending \"wrong token\"");
			output.flush();
			return Optional.empty();

		}

		ServerGUI.print("date is " + date);

		Optional<Long> sessionID = Optional.ofNullable(null);
		try {
			ReturnObject returnObject = cpw.checkToken(token);
			warnings.warning(input, output, returnObject);
			sessionID = returnObject.getSessionID();

		} catch (SQLException e) {
			ServerGUI.print(
					"if exception is \"java.sql.SQLNonTransientConnectionException: Could not send query: Last packet not finished\". Check if the SQL DB is up");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		token = null;
		ServerGUI.print("Optional sessionID is " + sessionID.isPresent());
		if (sessionID.isPresent()) {
			output.writeUTF("Correct Token");
			ServerGUI.print("sending \"Correct token\"");
			output.flush();
			return sessionID;
		} else {
			output.writeUTF("Wrong token");
			ServerGUI.print("sending \"wrong token\"");
			output.flush();
			return Optional.empty();

		}

	}

	Optional<Long> checkPW(DataInputStream input, DataOutputStream output, String ipAddress) throws IOException {
		ServerGUI.print("in checkPW()");
//		ConnectionToDB conn = ConnectionToDB.createConn();
		CheckLoginData cpw = new CheckLoginData(conn);
		String username = input.readUTF();
		ServerGUI.print("username is " + username);
		char[] pw = readArray(input);

//		IntStream s = IntStream.of(t)
//		StringBuilder stream = CharBuffer.wrap(pw).chars().collect(StringBuilder::new, StringBuilder::appendCodePoint,  StringBuilder::append);
//		IntStream stream = CharBuffer.wrap(pw).chars().collect(supplier, accumulator, combiner);
//		String deleteThis = Stream.of(pw).collect(Collector.of("", accumulator, combiner, finisher, characteristics));
//		ServerGUI.print("PW is "+ stream.toString());

		// send back correct PW
		// send back new token
		String token = null;
		Optional<Long> sessionID = Optional.ofNullable(null);
		try {
			ReturnObject returnObject = cpw.checkPassword(ipAddress ,username, pw);
			warnings.warning(input, output, returnObject);
			sessionID = returnObject.getSessionID();
			ServerGUI.print("SessionID " + sessionID);
			if (sessionID.isPresent()) {
				token = returnObject.getToken();
//						cpw.getToken(sessionID.get());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServerGUI.print("sessionID.isPresent() is " + sessionID.isPresent());
		ServerGUI.print("!token.isEmpty() is " + !token.isEmpty());
		ServerGUI.print("sessionID.isPresent() && !token.isEmpty() is " + (sessionID.isPresent() && !token.isEmpty()));
		if (sessionID.isPresent() && !token.isEmpty()) {
			ServerGUI.print("Correct pw");
			output.writeUTF("Correct pw");
			ServerGUI.print(token);
			output.writeUTF(token);
		} else {
			ServerGUI.print("wrong pw");
			output.writeUTF("wrong pw");
		}

		// TODO Auto-generated method stub
		return sessionID;
	}

	void saveMeasurement(Optional<Long> sessionID, ObjectInputStream inputFromClient)
			throws ClassNotFoundException, IOException {
		if (sessionID.isPresent()) {
			ServerGUI.print(" reading object");
			// Continuously serve the client
			BasicMeasurements object = (BasicMeasurements) inputFromClient.readObject();
			ServerGUI.print(" read object");

			ServerGUI.print(" TableName received from client: " + object.getTableName());
			ServerGUI.print(" first item in the data set: " + object.toString());

			SaveMethod sm = new SaveMethod(conn);
			sm.SaveData(object, sessionID.get());
		} else {
			throw new IllegalArgumentException(
					"Optional<Ineger> sessionID is required. please make sure it pass thru the password check or token test");
		}
	}

	void changePW(DataInputStream input, DataOutputStream output, String ipAddress, long sessionID) throws IOException {
		char[] oldPW = readArray(input);
		char[] pw = readArray(input);
		char[] pw2 = readArray(input);

		boolean equal = Arrays.equals(pw, pw2);
		ServerGUI.print("pw equals pw2 is " + equal);
		if (!equal) {
			output.writeUTF("different pw's");
		}
		
		CheckLoginData cld = new CheckLoginData(conn);
		
		try {
			boolean correctOldPW = cld.checkPassword(ipAddress, sessionID, oldPW);
			
			if(correctOldPW) {
//				changePW(sessionID,pw);
				
				
			} else {
				output.writeUTF("wrong old PW");
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}

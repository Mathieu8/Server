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

	private void sendStrings(String category, String message, DataOutputStream output) throws IOException {
		output.writeUTF(category);
		output.writeUTF(message);
	}

	private char[] readArray(DataInputStream input) throws IOException {
		ServerGUI.print("ServerOptions : readArray");
		int length = input.readInt();
		ServerGUI.print("ServerOptions : length = " + length);

		char[] array = new char[length];

		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < length; i++) {
			var c = input.readChar();
			array[i] = c;
			temp.append(c);
		}
		ServerGUI.print("ServerOptions : array is " + temp.toString());

		return array;

	}

	Optional<Long> sendNewAccount(DataInputStream input, DataOutputStream output) throws IOException {
		ServerGUI.print("ServerOptions: in checkPW()");
		ConnectionToDB conn = ConnectionToDB.createConn();
		CheckLoginData cpw = new CheckLoginData(conn);
		String username = input.readUTF();

		char[] pw = readArray(input);

		char[] pw2 = readArray(input);

		boolean equal = Arrays.equals(pw, pw2);
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

			output.writeUTF(massage);
			if (massage.equals("welcome")) {

				output.writeUTF(returnObject.getToken());

				return Optional.ofNullable(null);
//			} else {
//				token = cpw.getToken(sessionID.get());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (sessionID.isPresent() && !token.isEmpty()) {
			output.writeUTF("welcome");
			output.writeUTF(token);
		}

		return sessionID;
	}

	Optional<Long> checkToken(DataInputStream input, DataOutputStream output) throws IOException {
		ServerGUI.print("ServerOptions: In CheckToken");
		ConnectionToDB conn = ConnectionToDB.createConn();
		CheckLoginData cpw = new CheckLoginData(conn);
		char[] token = readArray(input);

		String date = "";
		try {
			date = input.readUTF();
		} catch (EOFException e) {
			// do nothing
			output.writeUTF("wrong token");
			// serverGUI.print("sending \"wrong token\"");
			output.flush();
			return Optional.empty();

		}

		// serverGUI.print("date is " + date);

		Optional<Long> sessionID = Optional.ofNullable(null);
		try {
			ReturnObject returnObject = cpw.checkToken(token);
			warnings.warning(input, output, returnObject);
			sessionID = returnObject.getSessionID();

		} catch (SQLException e) {
			// serverGUI.print(
//					"if exception is \"java.sql.SQLNonTransientConnectionException: Could not send query: Last packet not finished\". Check if the SQL DB is up");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		token = null;
		// serverGUI.print("Optional sessionID is " + sessionID.isPresent());
		if (sessionID.isPresent()) {
			output.writeUTF("correct token");
			// serverGUI.print("sending \"Correct token\"");
			output.flush();
			return sessionID;
		} else {
			output.writeUTF("wrong token");
			// serverGUI.print("sending \"wrong token\"");
			output.flush();
			return Optional.empty();

		}

	}

	Optional<Long> checkPW(DataInputStream input, DataOutputStream output, String ipAddress) throws IOException {
		ServerGUI.print("ServerOptions: in checkPW()");
		CheckLoginData cpw = new CheckLoginData(conn);
		String username = input.readUTF();
		char[] pw = readArray(input);

		for (int i = 0; i < pw.length; i++) {
			ServerGUI.print("ServerOptions: pw " + pw[i]);
		}

		// send back correct PW
		// send back new token
		String token = null;
		Optional<Long> sessionID = Optional.ofNullable(null);
		try {
			ReturnObject returnObject = cpw.checkPassword(ipAddress, username, pw);
			warnings.warning(input, output, returnObject);
			sessionID = returnObject.getSessionID();
			ServerGUI.print("SessionID " + sessionID);
			if (sessionID.isPresent()) {
				token = returnObject.getToken();
//						cpw.getToken(sessionID.get());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (sessionID.isPresent() && !token.isEmpty()) {
			sendStrings("token", token, output);
			output.writeUTF("correct pw");

		} else {
			output.writeUTF("wrong pw");
		}

		return sessionID;
	}

	void saveMeasurement(Optional<Long> sessionID, ObjectInputStream inputFromClient)
			throws ClassNotFoundException, IOException {
		if (sessionID.isPresent()) {
			// serverGUI.print(" reading object");
			// Continuously serve the client
			BasicMeasurements object = (BasicMeasurements) inputFromClient.readObject();
			ServerGUI.print("ServerOptions:  read object");
			ServerGUI.print("ServerOptions:  TableName received from client: " + object.getTableName());
			ServerGUI.print("ServerOptions:  first item in the data set: " + object.toString());

			SaveMethod sm = new SaveMethod(conn);
			sm.SaveData(object, sessionID.get());
		} else {
			throw new IllegalArgumentException(
					"Optional<Ineger> sessionID is required. please make sure it pass thru the password check or token test");
		}
	}

	void changePW(DataInputStream input, DataOutputStream output, String ipAddress) throws IOException {
		inner: {
			ServerGUI.print("ServerOptions: in changePW()");

			String username = input.readUTF();
			char[] oldPW = readArray(input);
			char[] pw = readArray(input);
			char[] pw2 = readArray(input);

			boolean equal = Arrays.equals(pw, pw2);
			if (!equal) {
				output.writeUTF("different pw's");
				break inner;
			}

			CheckLoginData cld = new CheckLoginData(conn);

			try {
				var returnObject = cld.checkPassword(ipAddress, username, oldPW);
				Optional<Long> sessionID = returnObject.getSessionID();
				boolean correctOldPW = sessionID.isPresent();

				if (correctOldPW) {
					returnObject = cld.insertNewPW(username, pw2);
					String message = returnObject.getMessage();
					ServerGUI.print("ServerOptions: " + message);
					sendStrings("token", returnObject.getToken(), output);
					output.writeUTF(message);

				} else {
					output.writeUTF("wrong old PW");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

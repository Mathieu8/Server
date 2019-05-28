package database.hash;

import java.util.Arrays;

public class Password {
	private String hashedPassword;
	private String hashedAlgorithm;
	private String token;
	private byte[] salt;

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String password) {
		hashedPassword = password;
	}

	public byte[] getSalt() {
		byte[] temp = new byte[salt.length];
		for (int i = 0; i < salt.length; i++) {
			temp[i] = salt[i];
		}

		return temp;
	}

	public void setSalt(byte[] salt) {
		byte[] temp = new byte[salt.length];
		for (int i = 0; i < salt.length; i++) {
			temp[i] = salt[i];
		}
		this.salt = temp;

	}

	public void resetPassword() {
		hashedPassword = "";
		Arrays.fill(salt, (byte) 0);
		hashedAlgorithm = "";

	}

	public String getHashedAlgorithm() {
		return hashedAlgorithm;
	}

	public void setHashedAlgorithm(String hashedAlgorithm) {
		this.hashedAlgorithm = hashedAlgorithm;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

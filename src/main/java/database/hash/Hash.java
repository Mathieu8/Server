package database.hash;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
//import java.util.Base64;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Server.ServerGUI;

//import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.apache.commons.lang3.builder.ToStringStyle;

public class Hash {
	private static String PREFERED_ALGORITHM = "SHA256";

	public Password newPW(String username, char[] pw) {
		System.out.println("in newPW("+username+" , pw)" );
		Password temp = new Password();
		temp = hashPW("", PREFERED_ALGORITHM, username, pw);

		Arrays.fill(pw, '0');
		temp.setHashedAlgorithm(PREFERED_ALGORITHM);
		// TODO Auto-generated catch block
		return temp;
	}
	


	public Optional<Long> checkPW(String salt, char[] pw, String hashPW, String hashAlgorithm) {
		
		Password temp = hashPW(salt, hashAlgorithm, "", pw);
		String userGivenHashPW = temp.getHashedPassword();
		System.out.println("user given hashed PW " + userGivenHashPW);
		System.out.println("DB given hashed PW   " + hashPW);
		boolean loginValid = slowEquals(userGivenHashPW, hashPW);
		temp.resetPassword();
		Optional<Long> sessionID = Optional.ofNullable(null);
		if (loginValid) {
			sessionID = Optional.of(tokenHash());
		}

//		Arrays.fill(salt, (byte) 0);
		Arrays.fill(pw, '0');
		return sessionID;
	}

	private Password hashPW(String saltString, String algorithm, String username, char[] pw) {
		System.out.println("in hashPW(" + saltString+" , "+algorithm+" , "+username+ " , pw)");
		HashAlgorithm hashAlgorithm = gatHashAlgorithm(algorithm);
		byte[] salt = { 0 };
		if (saltString.length() == 0) {
			try {
				salt = hashAlgorithm.getSalt(username);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			salt = hashAlgorithm.returnSalt(saltString);
		}
		Password temp = new Password();
		temp.setSalt(salt);
		String hashedPassword = hashAlgorithm.hashPW(salt, pw);
		System.out.println("hashPW is " + hashedPassword);

		temp.setHashedPassword(hashedPassword);

		// for each hash algoritm run it
		Arrays.fill(salt, (byte) 0);
		Arrays.fill(pw, '0');
		return temp;
	}

	private HashAlgorithm gatHashAlgorithm(String hashAlgorithm) {
		switch (hashAlgorithm) {
		case "none":
		case "test":
			System.out.println("none or test");
			return new TestHashAlgorithm();
		case "SHA256":
			System.out.println("SHA256");
			return new SHA256();

		}

		return null;
	}

	private boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}

	private boolean slowEquals(String a, String b) {
		int diff = a.length() ^ b.length();
//		System.out.println("a.length() ^ b.length() is " + (a.length() ^ b.length()));
		try {
			for (int i = 0; i < a.length() && i < b.length(); i++) {
				Thread.sleep(1);
				diff |= a.charAt(i) ^ b.charAt(i);
//				System.out.println("a.charAt(i) ^ b.charAt(i) is " + (a.charAt(i) ^ b.charAt(i)));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("diff == 0 is " + (diff == 0));
		return diff == 0;
	}

	public Optional<Long> tokenHash(Integer integer) {
		// TODO Auto-generated method stub
		return null;
	}

	private Long tokenHash() {
		Long r = new Random().nextLong();
		new Random();

		return r;
	}

}

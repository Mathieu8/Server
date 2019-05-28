package database.hash;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.primitives.Bytes;

public class SHA256 implements HashAlgorithm {

	@Override
	public byte[] getSalt(String username) throws NoSuchAlgorithmException {
		// Always use a SecureRandom generator
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		// Create array for salt
		byte[] salt = username.getBytes();
		if (salt.length < 16) {
			byte[] temp = new byte[16];

			for (int i = 0; i < salt.length; i++) {
				temp[i] = salt[i];
			}
			salt = temp;
		}
		// Get a random salt
		sr.nextBytes(salt);
		// return salt
		return salt;
	}

	@Override
	public String hashPW(byte[] salt, char[] pw) {
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// Add password bytes to digest
			md.update(salt);
			// Get the hash's bytes
			byte[] bytes = md.digest(pw.toString().getBytes());
			Arrays.fill(pw, (char) 0); // clear sensitive data
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
			Arrays.fill(bytes, (byte) 0);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	@Override
	public byte[] returnSalt(String string) {
		ArrayList<Byte> list1 = new ArrayList<>();
		byte[] salt = new byte[16];
		System.out.println(string);

		StringBuilder temp = new StringBuilder(string);
		for (int k = 0;;) {

			int i = 0;
			int j = temp.indexOf(",");
			System.out.println("indexOf(,) is " + j);
			System.out.println("stringbuilder so far " + temp);

			if (j == -1) {
				break;
			}
			list1.add(Byte.valueOf(temp.substring(i, j)));

			System.out.println("list1[" + k + "] is " + list1.get(k));
//			salt[k] = Byte.valueOf(temp.substring(i, j));
//			System.out.println("salt["+k+"] is "+salt[k]);
			k++;
			i = j;
			temp.delete(0, i + 1);

		}
		salt = Bytes.toArray(list1);
		System.out.println("string is " + string);
		System.out.print("   salt = ");
		for (byte b : salt) {
			System.out.print(b + ",");
		}
		System.out.println();

		return salt;
	}

}

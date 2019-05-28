package database.hash;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;


public class Test {

	public static void main(String[] args) {
		 SecureRandom secureRandom = new SecureRandom();
		    byte[] token = new byte[20];
		    secureRandom.nextBytes(token);
		    System.out.println( Base64.getUrlEncoder().withoutPadding().encodeToString(token));
		
		Hash hash = new Hash();
		String username = "test";
		char[] pw = { 't', 'e', 's', 't' };
		Password password = hash.newPW(username, pw);
		System.out.println("hashedPassword is " + password.getHashedPassword());
		System.out.print("salt is ");
		byte[] salt = password.getSalt();
		String hashedPW = password.getHashedPassword();

		StringBuilder temp = new StringBuilder();
		for (byte b : salt) {
			temp.append(b + ",");

		}
		
		SHA256 checkPW = new SHA256();
		byte[] newSalt = checkPW.returnSalt(temp.toString());
		System.out.print("newSalt = ");
		for (byte b : newSalt) {
			System.out.print(b + " , ");
		}
		System.out.println();

		Optional<Long> password1 = hash.checkPW(temp.toString(), pw, hashedPW, "SHA256");
		System.out.println("loginValid is " + password1.isPresent());
		System.out.println("loginValid is " + password1.get());

		// TODO Auto-generated method stub

	}

//	private static byte[] returnSalt(String string) {
//		byte[] salt = new byte[16];
//
//		StringBuilder temp = new StringBuilder(string);
//		for (int k = 0; k < 16;) {
//			
//			int i = 0;
//			int j = temp.indexOf(",");
//
//			if (j == -1 && j >= string.length()) {
//				break;
//			}
//			salt[k] = Byte.valueOf(temp.substring(i, j));
//			k++;
//			i = j;
//			temp.delete(0, i + 1);
//
//		}
//
//		return salt;
//	}

}

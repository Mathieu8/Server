package database.hash;

import java.util.Optional;

public class GoesRight {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Hash hash = new Hash();
		String username = "ww=@ict-stadsbrug.nl";
		
		String tempPW= "@ict-stadsbrug.nl";
		char[] pw = new char[tempPW.length()];
		for(int i =0;i<pw.length;i++) {
			pw[i]=tempPW.charAt(i);
		}
		
		
		Password password = hash.newPW(username, pw);
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
			System.out.print(b + ",");
		}
		System.out.println();
		System.out.println("from here checking PW");
		System.out.println();
		for(int i =0;i<pw.length;i++) {
			pw[i]=tempPW.charAt(i);
		}
		print(pw);

		Optional<Long> password1 = hash.checkPW(temp.toString(), pw, hashedPW, "SHA256");
		System.out.println("loginValid is " + password1.isPresent());
		System.out.println("loginValid is " + password1.get());
	}
	
	static void print(char[] pw) {
		for(char c : pw) {
			System.out.print(c);
		}
		System.out.println();
	}

}

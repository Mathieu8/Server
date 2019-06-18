package database.hash;

import java.util.Optional;

public class GoesWrong {
	public static void main(String[] args) {
		Hash hash = new Hash();
		String tempPW = "@ict-stadsbrug.nl";
		char[] pw = new char[tempPW.length()];
		for (int i = 0; i < pw.length; i++) {
			pw[i] = tempPW.charAt(i);
		}

		String hashPW = "8c20698a992b8aa81e4190c633905b56d7e55a755ebc360aac9b4026ffc65d7b";
		String saltString = "-28,-99,41,-30,102,-11,110,87,87,0,-90,-126,95,-57,26,-6,-18,-110,";
		
		print(pw);

		Optional<Long> password1 = hash.checkPW(saltString, pw, hashPW, "SHA256");
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

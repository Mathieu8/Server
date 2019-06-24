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

		String hashPW = "4902a8d29ff09baae09b9e8d6276539838c35f00666f8cc3579a7480f503c4c5";
		String saltString = "-32,-40,-17,-92,122,-95,-85,86,40,126,-37,-19,-65,-83,-20,118,-11,-67,45,66,";
		
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

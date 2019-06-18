package database.hash;

import java.util.Optional;

public class Test {
	public static void main(String[] args) {
		Hash hash = new Hash();
		String tempPW = "@ict-stadsbrug.nl";
		char[] pw = new char[tempPW.length()];
		for (int i = 0; i < pw.length; i++) {
			pw[i] = tempPW.charAt(i);
		}

		Password pw1 = new Password();
		pw1.setHashedAlgorithm("SHA256");
		pw1.setHashedPassword("d7b2aeb1bbb3802b04c9fb274f922e8f918fc63a64e31763981e91df6a9299a9");
		Optional<Long> password1 = hash.checkPW(
				"-45,-91,59,52,126,-112,-110,-78,-53,-22,108,102,-123,35,-32,-56,-33,30,-1,63,", pw, pw1.getHashedPassword(), pw1.getHashedAlgorithm());
		System.out.println("loginValid is " + password1.isPresent());
		System.out.println("loginValid is " + password1.get());
	}
}
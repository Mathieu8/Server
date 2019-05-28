package database.hash;

import java.security.NoSuchAlgorithmException;

public interface HashAlgorithm {
	byte[] getSalt(String username) throws NoSuchAlgorithmException;
	
	String hashPW(byte[] salt, char[] pw );
	
	byte[] returnSalt(String string);
		
}

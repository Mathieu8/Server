package database.hash;


public class TestHashAlgorithm implements HashAlgorithm {

	@Override
	public byte[] getSalt(String username)  {
		byte[] salt = new byte[16];
		for(int i=0;i<16;i++) {
			salt[i] = (byte)username.charAt(i);
		}
		return salt;
	}

	@Override
	public String hashPW(byte[] salt, char[] pw) {
		
		StringBuilder temp = new StringBuilder();
		for (char c : pw) {
			temp.append(c);
		}
		// TODO Auto-generated method stub
		return temp.toString();
	}

	@Override
	public byte[] returnSalt(String string) {
		// TODO Auto-generated method stub
		byte[] salt = { 0};
		return salt;
	}

}

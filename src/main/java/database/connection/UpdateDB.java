package database.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import Server.ServerGUI;

public interface UpdateDB extends PassConnection {
	default void updateDB(String db, String[] fields, String[] data, String criteria) throws SQLException {
		if (fields.length != data.length) {
			throw new ArrayIndexOutOfBoundsException("length of fields  and data doesn't make sense");
		}

		StringBuilder query = new StringBuilder("UPDATE `" + db + "` SET ");

		for (int i = 0; i < fields.length; i++) {
			query.append("`" + fields[i] + "`='" + data[i] + "',");
		}
		query.deleteCharAt(query.lastIndexOf(","));

		query.append("WHERE " + criteria);

		excutePreparedStatement(query.toString());
	}

}

//UPDATE `users` SET `password`='f5bd36d99817515c36735c4184f3499a6f3e9e7046f6b8caeb2f38ac00712a14',`passwordSalt`='105,-2,61,16,-34,26,35,-24,42,-35,-76,44,51,-14,36,67,-54,29,-37,-107,',`passwordHashAlgorithm`='SHA256' WHERE `Email`= 'ww=@ict-stadsbrug.nl'
//UPDATE `users` SET `password`='f5bd36d99817515c36735c4184f3499a6f3e9e7046f6b8caeb2f38ac00712a14',`passwordSalt`='105,-2,61,16,-34,26,35,-24,42,-35,-76,44,51,-14,36,67,-54,29,-37,-107,',`passwordHashAlgorithm`='SHA256',WHERE `Email`= 'ww=@ict-stadsbrug.nl'
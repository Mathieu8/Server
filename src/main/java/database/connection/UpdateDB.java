package database.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import Server.ServerGUI;

public interface UpdateDB extends PassConnection {
	default void updateDB(String db, String[] fields, String[] data, String criteria) throws SQLException {
		if (fields.length != data.length) {
			throw new ArrayIndexOutOfBoundsException("length of fields  and data doesn't make sense");
		}

		StringBuilder query = new StringBuilder("UPDATE `smtdb`.`" + db + "` SET");

		for (int i = 0; i < fields.length; i++) {
			query.append("`" + fields[i] + "`=" + data[i] + ",");
		}

		query.append("WHERE " + criteria);

		excutePreparedStatement(query.toString());
	}

}

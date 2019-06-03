package database.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface CreateDB extends PassConnection {
	
	/**
	 * @param db
	 * @param fields
	 * @param data
	 * @throws SQLException
	 */
	default void createDB(String db, String[] fields, String[] data/*, String criteria*/) throws SQLException {
		if (fields.length != data.length) {
			throw new ArrayIndexOutOfBoundsException("length of fields  and data doesn't make sense");
		}
		
		StringBuilder query = new StringBuilder("INSERT INTO `smtdb`.`" + db + "` (");
		
		for (int i = 0; i < fields.length; i++) {
			query.append("`"+fields[i]+ "`,");
		}
		query.deleteCharAt(query.lastIndexOf(","));
		
		query.append(") VALUES (");
		
		for (int i = 0; i < fields.length; i++) {
			query.append("'" + data[i]+"',");
		}
		query.deleteCharAt(query.lastIndexOf(","));
		query.append(')');
		
//		query.append(criteria);
		

		 excutePreparedStatement(query.toString());
	}

	/**
	 * @param db
	 * @param data
	 * @throws SQLException
	 */
	default void createDB(String db, String data) throws SQLException {
		
		String query = "INSERT `smtdb`.`" + db + "` SET " + data + ";";
		
		excutePreparedStatement(query);
	}
	
	
	default void createDB( String query) throws SQLException {		
		excutePreparedStatement(query);
	}
	
	
}

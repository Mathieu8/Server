package database.connection;

import java.sql.SQLException;

public interface DeleteDB extends PassConnection {
	default void delete(String db, String conditions) throws SQLException {
		String query = "DELETE FROM `smtdb`.`" + db + " WHERE " + conditions;
		excutePreparedStatement(query);

	}

	default void delete(String db, String[] fields, String[] condition) throws SQLException {
		if (fields.length != condition.length) {
			throw new ArrayIndexOutOfBoundsException("length of fields  and data doesn't make sense");
		}
		StringBuilder conditions = new StringBuilder();
		for(int i = 0; i<fields.length;i++) {
			conditions.append(" \'");
			conditions.append(fields[i]);
			conditions.append("\' = ");
			conditions.append(condition[i]);
		}
		
		String query =  "DELETE FROM `smtdb`.`" + db + " WHERE " + conditions;
		excutePreparedStatement(query);

	}
}

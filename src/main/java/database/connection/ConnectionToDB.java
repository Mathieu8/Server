package database.connection;

import java.nio.CharBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionToDB implements CreateDB, ReadDB, UpdateDB, DeleteDB {
	Connection conn = null;
	static private ConnectionToDB connToDB;
	private Long time = System.nanoTime();
//	private Statement stmt;

	private ConnectionToDB() {
	};

	/**
	 * This method creates a Connection to SMTDB. After that all methods within the
	 * SaveMethod class can use it
	 * 
	 * @return
	 * 
	 * 
	 * @throws SQLException        - if a database access error occurs or the url is
	 *                             null
	 * @throws SQLTimeoutException - when the driver has determined that the timeout
	 *                             value specified by the setLoginTimeout method has
	 *                             been exceeded and has at least tried to cancel
	 *                             the current database connection attempt
	 */
	public synchronized static ConnectionToDB createConn() {
		if (connToDB == null) {
			connToDB = new ConnectionToDB();
			String dbName = "jdbc:mysql:";
			String server = "//ict-stadsbrug.nl/smtdb";
			String user = "smt";
			String pw = "12345";

			try {
				connToDB.conn = DriverManager.getConnection(dbName + server, user, pw);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connToDB;
	}

	public boolean checkConn() throws SQLException {
		if (conn != null ? true : false) {
			if (time + 60_000_000 < System.nanoTime()) {
				return true;
			} else {
				closeConn();
			}
		}
		return false;
	}

	public void excuteStamenet(String query) throws SQLException {
		if (checkConn()) {
			PreparedStatement st = conn.prepareStatement(query);
			st.executeUpdate();
			st.close();
		}
	}

	/**
	 * does what it's name suggest, closes the connection to the database
	 * 
	 * @param conn
	 * @return Connection that is closed
	 * @throws SQLException - SQLException if a database access error occurs
	 * 
	 */
	protected void closeConn() throws SQLException {
		if (conn != null) {
			conn.close();
			time = null;
		}
	}

	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return conn;
	}

	

}

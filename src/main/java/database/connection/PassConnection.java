package database.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Server.ServerGUI;


public interface PassConnection {
	Connection getConnection();

	boolean checkConn() throws SQLException;
	
	default void excutePreparedStatement(String query) throws SQLException {
		PreparedStatement st = getConnection().prepareStatement(query);
		ServerGUI.print(query);
		st.execute();
	}
	
	default ResultSet getResultSet(String query) throws SQLException {
		ServerGUI.print(query);
		PreparedStatement st = getConnection().prepareStatement(query);
		ResultSet rs = st.executeQuery(query);
		return rs;
	}
}

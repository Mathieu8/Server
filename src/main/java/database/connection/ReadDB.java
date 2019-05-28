package database.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Server.ServerGUI;

public interface ReadDB extends PassConnection {

	public default List<String> readStringDB(String query) throws SQLException {
		List<String> list = new ArrayList<String>();
		if (checkConn()) {
//			PreparedStatement st = getConnection().prepareStatement(query);
			Statement st = getConnection().createStatement();
//			st.execute(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.executeQuery(query); // st.getGeneratedKeys();
			int i = 1;
			if (rs.next()) {
				list.add(rs.getString(i++));
			} else {
				// throw an exception from here
				ServerGUI.print("somthing went wrong");
				throw new SQLException();
			}
		}
		return list;
	}

	public default List<Integer> readIntDB(String query) throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		if (checkConn()) {
			Statement st = getConnection().prepareStatement(query);
//			Statement st = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.executeQuery(query);
//			ResultSet rs = st.getGeneratedKeys();

			int i = 1;
			rs.beforeFirst();
			while (rs.next()) {
				list.add(rs.getInt(i++));
			}
		}
		return list;

//		Statement st = conn.createStatement();
//		st.execute(query, Statement.RETURN_GENERATED_KEYS);
//		int UID = -1;
//
//		ResultSet rs = st.getGeneratedKeys();
//
//		ServerGUI.print(rs.getRow());
//		if (rs.next()) {
//			UID = rs.getInt(1);
	}

	public default List<Long> readLongDB(String query) throws SQLException {
		List<Long> list = new ArrayList<Long>();
		if (checkConn()) {
			Statement st = getConnection().prepareStatement(query);
//			Statement st = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.executeQuery(query);
//			ResultSet rs = st.getGeneratedKeys();

			int i = 1;
			while (rs.next()) {
				list.add(rs.getLong(i++));
			}
		}
		return list;
	}

}

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
		ServerGUI.print("quetry = " + query);
		if (checkConn()) {
			Statement st = getConnection().createStatement();
			ResultSet rs = st.executeQuery(query); 
			int i = 1;
			if (rs.next()) {
				list.add(rs.getString(i++));
			} else {
				// throw an exception from here
				ServerGUI.print("somthing went wrong");
				throw new SQLException();
			}
		}
		for(String i : list) {
			ServerGUI.print(i+ "");
		}
		return list;
	}

	public default List<Integer> readIntDB(String query) throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		if (checkConn()) {
			ServerGUI.print("quetry = " + query);
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
		for(int i : list) {
			ServerGUI.print(i+ "");
		}
		return list;

	}

	public default List<Long> readLongDB(String query) throws SQLException {
		List<Long> list = new ArrayList<Long>();
		ServerGUI.print("quetry = " + query);
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
		for(Long i : list) {
			ServerGUI.print(i+ "");
		}
		return list;
	}

}
package org.unibl.etf.bp.jdbcprimeri;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCPrimer6 {

	public static void main(String[] args) {
		JDBCPrimer6 primer = new JDBCPrimer6();
		primer.prikaziPredmete();
	}

	private void prikaziPredmete() {
		Connection c = null;
		Statement s = null;
		ResultSet rs = null;
		try {
			c = ConnectionPool.getInstance().checkOut();
			s = c.createStatement();
			rs = s.executeQuery("select * from predmet");

			while (rs.next())
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " "
						+ rs.getByte(3) + " " + rs.getString(4));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (s != null)
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			ConnectionPool.getInstance().checkIn(c);
		}
	}
	
}

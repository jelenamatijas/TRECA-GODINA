package org.unibl.etf.bp.jdbcprimeri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCPrimer2 {

	public static void main(String[] args) {
		JDBCPrimer2 primer = new JDBCPrimer2();
		Scanner scanner = new Scanner(System.in);
		System.out.print("ECTS: ");
		byte ects = scanner.nextByte();
		scanner.close();
		primer.prikaziPredmete(ects);
	}

	private void prikaziPredmete(byte ects) {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/uniis", "student", "student");
			ps = c.prepareStatement("select * from predmet where ECTS=? order by NazivPredmeta");
			ps.setByte(1, ects);
			rs = ps.executeQuery();
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
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (c != null)
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
}

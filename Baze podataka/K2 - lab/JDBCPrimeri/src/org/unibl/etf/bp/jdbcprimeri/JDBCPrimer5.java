package org.unibl.etf.bp.jdbcprimeri;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCPrimer5 {

	public static void main(String[] args) {
		JDBCPrimer5 primer = new JDBCPrimer5();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Id studijskog programa: ");
		int idSP = scanner.nextInt();
		scanner.close();
		primer.prosecneOceneStudenataPoSP(idSP);
	}

	private void prosecneOceneStudenataPoSP(int idSP) {
		Connection c = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/uniis", "student", "student");
			cs = c.prepareCall("{call prosek_ocena_sp(?)}");
			cs.setInt(1, idSP);
			rs = cs.executeQuery();
			while (rs.next())
				System.out.println(rs.getString(1) + " " + rs.getString(2)
						+ " " + rs.getString(3) + " " + rs.getDouble(4));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (cs != null)
				try {
					cs.close();
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

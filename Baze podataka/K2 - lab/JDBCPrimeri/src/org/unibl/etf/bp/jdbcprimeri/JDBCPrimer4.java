package org.unibl.etf.bp.jdbcprimeri;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class JDBCPrimer4 {

	public static void main(String[] args) {
		JDBCPrimer4 primer = new JDBCPrimer4();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Matični broj studenta: ");
		String jmb = scanner.nextLine();
		System.out.print("Id studijskog programa: ");
		int idSP = scanner.nextInt();
		scanner.close();

		Double prosek = primer.prosekOcena(jmb, idSP);
		if (prosek != null)
			System.out.println("Prosek ocena je: " + prosek);
		else
			System.out.println("Prosek ocena ne može da se izračuna.");
	}

	private Double prosekOcena(String jmb, int idSP) {
		Double rez = null;
		Connection c = null;
		CallableStatement cs = null;
		try {
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/uniis", "student", "student");
			cs = c.prepareCall("{call prosek_ocena(?, ?, ?)}");
			cs.setString(1, jmb);
			cs.setInt(2, idSP);
			cs.registerOutParameter(3, Types.DOUBLE);

			cs.executeUpdate();

			rez = (Double) cs.getObject(3);
			// rez = cs.getDouble(3);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
		return rez;
	}
	
}

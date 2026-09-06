package org.unibl.etf.bp.jdbcprimeri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class JDBCPrimer3 {

	public static void main(String[] args) {
		JDBCPrimer3 primer = new JDBCPrimer3();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Id predmeta: ");
		int idPredmeta = scanner.nextInt();
		System.out.print("Naziv predmeta: ");
		scanner.nextLine();
		String nazivPredmeta = scanner.nextLine();
		System.out.print("ECTS: ");
		byte ects = scanner.nextByte();
		System.out.print("Naziv matičnog fakulteta: ");
		scanner.nextLine();
		String nazivFakulteta = scanner.nextLine();
		scanner.close();
		primer.dodajPredmet(idPredmeta, nazivPredmeta, ects, nazivFakulteta);
	}

	private void dodajPredmet(int idPredmeta, String nazivPredmeta, byte ects,
			String nazivFakulteta) {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/uniis?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false", "student", "student");
			ps = c.prepareStatement("insert into predmet values (?, ?, ?, ?)");
			ps.setInt(1, idPredmeta);
			ps.setString(2, nazivPredmeta);
			ps.setByte(3, ects);
			ps.setString(4, nazivFakulteta);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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

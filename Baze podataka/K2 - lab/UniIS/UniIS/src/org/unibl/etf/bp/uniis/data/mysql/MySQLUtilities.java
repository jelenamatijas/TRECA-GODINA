package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class MySQLUtilities {
	
	private static MySQLUtilities instance;

	public static MySQLUtilities getInstance() {
		if (instance == null)
			instance = new MySQLUtilities();
		return instance;
	}

	private MySQLUtilities() {
	}

	public void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close(Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close(Connection conn, Statement s) {
		close(s);
		close(conn);
	}

	public void close(Connection conn, ResultSet rs) {
		close(rs);
		close(conn);
	}

	public void close(Statement s, ResultSet rs) {
		close(rs);
		close(s);
	}

	public void close(Connection conn, Statement s, ResultSet rs) {
		close(rs);
		close(s);
		close(conn);
	}

	public String preparePattern(String text) {
		return text.replace('*', '%').replace('?', '_');
	}

	public void showSQLException(SQLException e) {
		JOptionPane.showMessageDialog(null, "Kod greške: " + e.getErrorCode()
				+ "\nPoruka: " + e.getMessage(), "Greška (baza podataka)",
				JOptionPane.ERROR_MESSAGE);
	}

	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Greška (baza podataka)",
				JOptionPane.ERROR_MESSAGE);
	}
	
}

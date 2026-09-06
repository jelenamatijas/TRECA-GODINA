package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.unibl.etf.bp.uniis.data.FakultetDataAccess;
import org.unibl.etf.bp.uniis.entity.Fakultet;

public class FakultetDataAccessImpl implements FakultetDataAccess {

	@Override
	public List<Fakultet> fakulteti(String nazivFakulteta) {
		List<Fakultet> retVal = new ArrayList<Fakultet>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT NazivFakulteta, Adresa "
				+ "FROM fakultet "
				+ "WHERE NazivFakulteta LIKE ? "
				+ "ORDER BY NazivFakulteta ASC ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1,
					MySQLUtilities.getInstance().preparePattern(nazivFakulteta));
			rs = ps.executeQuery();

			while (rs.next())
				retVal.add(new Fakultet(rs.getString("NazivFakulteta"), rs
						.getString("Adresa")));
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(ps, rs);
		}
		return retVal;
	}

	@Override
	public boolean dodajFakultet(Fakultet fakultet) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "INSERT INTO fakultet VALUES "
				+ "(?, ?) ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, fakultet.getNazivFakulteta());
			ps.setString(2, fakultet.getAdresa());

			retVal = ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(ps);
		}
		return retVal;
	}

	@Override
	public boolean azurirajFakultet(Fakultet fakultet) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "UPDATE fakultet SET "
				+ "Adresa=? "
				+ "WHERE NazivFakulteta=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, fakultet.getAdresa());
			ps.setString(2, fakultet.getNazivFakulteta());

			retVal = ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(ps);
		}
		return retVal;
	}

	@Override
	public boolean obrisiFakultet(String nazivFakulteta) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "DELETE FROM fakultet "
				+ "WHERE NazivFakulteta=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, nazivFakulteta);

			retVal = ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(ps);
		}
		return retVal;
	}

}

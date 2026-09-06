package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.unibl.etf.bp.uniis.data.PredmetDataAccess;
import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.Predmet;

public class PredmetDataAccessImpl implements PredmetDataAccess {

	@Override
	public Predmet predmet(int idPredmeta) {
		Predmet retVal = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta, Adresa "
				+ "FROM predmet "
				+ "NATURAL JOIN fakultet "
				+ "WHERE IdPredmeta=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idPredmeta);
			rs = ps.executeQuery();

			if (rs.next())
				retVal = new Predmet(rs.getInt(1), rs.getString(2),
						rs.getByte(3), new Fakultet(rs.getString(4),
								rs.getString(5)));
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
	public List<Predmet> predmeti(String nazivPredmeta, String nazivFakulteta) {
		List<Predmet> retVal = new ArrayList<Predmet>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta, Adresa "
				+ "FROM predmet "
				+ "NATURAL JOIN fakultet "
				+ "WHERE NazivPredmeta LIKE ? ";
		if (nazivFakulteta != null)
			query += "AND NazivFakulteta=? ";
		query += "ORDER BY NazivFakulteta ASC, NazivPredmeta ASC, IdPredmeta ASC ";

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1,
					MySQLUtilities.getInstance().preparePattern(nazivPredmeta));
			if (nazivFakulteta != null)
				ps.setString(2, nazivFakulteta);
			rs = ps.executeQuery();

			while (rs.next())
				retVal.add(new Predmet(rs.getInt(1), rs.getString(2), rs
						.getByte(3), new Fakultet(rs.getString(4), rs
						.getString(5))));
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
	public boolean dodajPredmet(Predmet predmet) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "INSERT INTO predmet VALUES "
				+ "(?, ?, ?, ?) ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, predmet.getIdPredmeta());
			ps.setString(2, predmet.getNazivPredmeta());
			ps.setByte(3, predmet.getEcts());
			ps.setString(4, predmet.getFakultet().getNazivFakulteta());

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
	public boolean azurirajPredmet(Predmet predmet) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "UPDATE predmet SET "
				+ "NazivPredmeta=?, "
				+ "ECTS=?, "
				+ "NazivFakulteta=? "
				+ "WHERE IdPredmeta=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, predmet.getNazivPredmeta());
			ps.setByte(2, predmet.getEcts());
			ps.setString(3, predmet.getFakultet().getNazivFakulteta());
			ps.setInt(4, predmet.getIdPredmeta());

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
	public boolean obrisiPredmet(int idPredmeta) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "DELETE FROM predmet "
				+ "WHERE IdPredmeta=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idPredmeta);

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

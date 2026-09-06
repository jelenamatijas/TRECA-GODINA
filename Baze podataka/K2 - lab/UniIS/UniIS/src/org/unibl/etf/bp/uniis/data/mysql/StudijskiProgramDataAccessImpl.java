package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.unibl.etf.bp.uniis.data.StudijskiProgramDataAccess;
import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;

public class StudijskiProgramDataAccessImpl implements StudijskiProgramDataAccess {

	@Override
	public StudijskiProgram studijskiProgram(int idSP) {
		StudijskiProgram retVal = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta, Adresa "
				+ "FROM studijski_program "
				+ "NATURAL JOIN fakultet "
				+ "WHERE IdSP=? ";

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idSP);
			rs = ps.executeQuery();

			if (rs.next())
				retVal = new StudijskiProgram(rs.getInt(1), rs.getString(2),
						rs.getByte(3), rs.getByte(4), rs.getShort(5),
						rs.getString(6), new Fakultet(rs.getString(7),
								rs.getString(8)));
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
	public List<StudijskiProgram> studijskiProgrami(String nazivSP,
			Byte ciklus, String nazivFakulteta) {
		List<StudijskiProgram> retVal = new ArrayList<StudijskiProgram>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta, Adresa "
				+ "FROM studijski_program "
				+ "NATURAL JOIN fakultet "
				+ "WHERE NazivSP LIKE ? ";
		if (ciklus != null)
			query += "AND Ciklus=? ";
		if (nazivFakulteta != null)
			query += "AND NazivFakulteta=? ";
		query += "ORDER BY NazivFakulteta ASC, Ciklus ASC, NazivSP ASC, IdSP ASC ";

		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, MySQLUtilities.getInstance().preparePattern(nazivSP));
			int i = 2;
			if (ciklus != null)
				ps.setByte(i++, ciklus.byteValue());
			if (nazivFakulteta != null)
				ps.setString(i++, nazivFakulteta);
			rs = ps.executeQuery();

			while (rs.next())
				retVal.add(new StudijskiProgram(rs.getInt(1), rs
						.getString(2), rs.getByte(3), rs.getByte(4), rs
						.getShort(5), rs.getString(6), new Fakultet(rs
						.getString(7), rs.getString(8))));
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
	public boolean dodajStudijskiProgram(StudijskiProgram studijskiProgram) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "INSERT INTO studijski_program VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?) ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, studijskiProgram.getIdSP());
			ps.setString(2, studijskiProgram.getNazivSP());
			ps.setByte(3, studijskiProgram.getCiklus());
			ps.setByte(4, studijskiProgram.getTrajanje());
			ps.setShort(5, studijskiProgram.getUkupanBrojEcts());
			ps.setString(6, studijskiProgram.getZvanje());
			ps.setString(7, studijskiProgram.getFakultet().getNazivFakulteta());

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
	public boolean azurirajStudijskiProgram(StudijskiProgram studijskiProgram) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "UPDATE studijski_program SET "
				+ "NazivSP=?, "
				+ "Ciklus=?, "
				+ "Trajanje=?, "
				+ "UkupnoECTS=?, "
				+ "Zvanje=?, "
				+ "NazivFakulteta=? "
				+ "WHERE IdSP=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setString(1, studijskiProgram.getNazivSP());
			ps.setInt(2, studijskiProgram.getCiklus());
			ps.setInt(3, studijskiProgram.getTrajanje());
			ps.setInt(4, studijskiProgram.getUkupanBrojEcts());
			ps.setString(5, studijskiProgram.getZvanje());
			ps.setString(6, studijskiProgram.getFakultet().getNazivFakulteta());
			ps.setInt(7, studijskiProgram.getIdSP());

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
	public boolean obrisiStudijskiProgram(int idSP) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "DELETE FROM studijski_program "
				+ "WHERE IdSP=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idSP);

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

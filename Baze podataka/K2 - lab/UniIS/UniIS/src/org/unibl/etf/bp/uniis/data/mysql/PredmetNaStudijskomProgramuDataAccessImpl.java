package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.unibl.etf.bp.uniis.data.PredmetNaStudijskomProgramuDataAccess;
import org.unibl.etf.bp.uniis.entity.Fakultet;
import org.unibl.etf.bp.uniis.entity.Predmet;
import org.unibl.etf.bp.uniis.entity.PredmetNaStudijskomProgramu;
import org.unibl.etf.bp.uniis.entity.StudijskiProgram;

public class PredmetNaStudijskomProgramuDataAccessImpl implements
		PredmetNaStudijskomProgramuDataAccess {

	@Override
	public List<PredmetNaStudijskomProgramu> predmetiNaSP(int idSP) {
		List<PredmetNaStudijskomProgramu> retVal = new ArrayList<PredmetNaStudijskomProgramu>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT P.IdPredmeta, NazivPredmeta, ECTS, PF.NazivFakulteta, PF.Adresa, "
				+ "SP.IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, SPF.NazivFakulteta, SPF.Adresa, "
				+ "Semestar, TipPredmeta "
				+ "FROM p_na_sp PSP "
				+ "INNER JOIN predmet P ON P.IdPredmeta=PSP.IdPredmeta "
				+ "INNER JOIN fakultet PF ON PF.NazivFakulteta=P.NazivFakulteta "
				+ "INNER JOIN studijski_program SP ON SP.IdSP=PSP.IdSP "
				+ "INNER JOIN fakultet SPF ON SPF.NazivFakulteta=SP.NazivFakulteta "
				+ "WHERE PSP.IdSP=? "
				+ "ORDER BY Semestar ASC, TipPredmeta DESC, NazivPredmeta ASC, IdPredmeta ASC ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idSP);
			rs = ps.executeQuery();

			while (rs.next())
				retVal.add(new PredmetNaStudijskomProgramu(new Predmet(rs
						.getInt(1), rs.getString(2), rs.getByte(3),
						new Fakultet(rs.getString(4), rs.getString(5))),
						new StudijskiProgram(rs.getInt(6), rs.getString(7),
								rs.getByte(8), rs.getByte(9), rs.getShort(10),
								rs.getString(11), new Fakultet(rs
										.getString(12), rs.getString(13))), rs
								.getByte(14), rs.getString(15)));
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
	public boolean dodajPredmetNaSP(PredmetNaStudijskomProgramu predmetNaSP) {
		boolean retVal = false;
		Connection conn = null;
		CallableStatement cs = null;

		String query = "{CALL dodaj_p_na_sp(?, ?, ?, ?, ?, ?)}";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			cs = conn.prepareCall(query);
			cs.setInt(1, predmetNaSP.getPredmet().getIdPredmeta());
			cs.setInt(2, predmetNaSP.getStudijskiProgram().getIdSP());
			cs.setByte(3, predmetNaSP.getSemestar());
			cs.setString(4, predmetNaSP.getTipPredmeta());
			cs.registerOutParameter(5, Types.BOOLEAN);
			cs.registerOutParameter(6, Types.VARCHAR);

			cs.execute();
			retVal = cs.getBoolean(5);
			if (!retVal)
				MySQLUtilities.getInstance().showErrorMessage(cs.getString(6));
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(cs);
		}
		return retVal;
	}

	@Override
	public boolean azurirajPredmetNaSP(
			PredmetNaStudijskomProgramu predmetNaSP) {
		boolean retVal = false;
		Connection conn = null;
		CallableStatement cs = null;

		String query = "{CALL azuriraj_p_na_sp(?, ?, ?, ?, ?, ?)}";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			cs = conn.prepareCall(query);
			cs.setInt(1, predmetNaSP.getPredmet().getIdPredmeta());
			cs.setInt(2, predmetNaSP.getStudijskiProgram().getIdSP());
			cs.setByte(3, predmetNaSP.getSemestar());
			cs.setString(4, predmetNaSP.getTipPredmeta());
			cs.registerOutParameter(5, Types.BOOLEAN);
			cs.registerOutParameter(6, Types.VARCHAR);

			cs.execute();
			retVal = cs.getBoolean(5);
			if (!retVal)
				MySQLUtilities.getInstance().showErrorMessage(cs.getString(6));
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(cs);
		}
		return retVal;
	}

	@Override
	public boolean obrisiPredmetNaSP(int idPredmeta, int idSP) {
		boolean retVal = false;
		Connection conn = null;
		PreparedStatement ps = null;

		String query = "DELETE FROM p_na_sp "
				+ "WHERE IdPredmeta=? AND IdSP=? ";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			ps = conn.prepareStatement(query);
			ps.setInt(1, idPredmeta);
			ps.setInt(2, idSP);

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

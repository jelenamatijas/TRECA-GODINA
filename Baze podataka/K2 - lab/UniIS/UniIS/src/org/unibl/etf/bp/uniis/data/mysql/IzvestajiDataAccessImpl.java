package org.unibl.etf.bp.uniis.data.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.unibl.etf.bp.uniis.data.IzvestajiDataAccess;

public class IzvestajiDataAccessImpl implements IzvestajiDataAccess {

	@Override
	public Vector<Vector<Object>> prosecneOcene() {
		Vector<Vector<Object>> retVal = new Vector<Vector<Object>>();
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;

		String query = "SELECT * FROM student_detaljno";
		try {
			conn = ConnectionPool.getInstance().checkOut();
			cs = conn.prepareCall(query);

			rs = cs.executeQuery();
			while (rs.next()) {
				Vector<Object> red = new Vector<Object>();
				red.add(rs.getString(1)); // JMB
				red.add(rs.getString(2)); // Prezime i ime
				red.add(rs.getString(3)); // NazivSP
				red.add(rs.getByte(4)); // Ciklus
				red.add(rs.getDouble(5)); // Prosek
				retVal.add(red);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			MySQLUtilities.getInstance().showSQLException(e);
		} finally {
			ConnectionPool.getInstance().checkIn(conn);
			MySQLUtilities.getInstance().close(cs, rs);
		}
		return retVal;
	}

}

package org.unibl.etf.bp.uniis.data;

import org.unibl.etf.bp.uniis.data.mysql.MySQLDataAccessFactory;

public abstract class DataAccessFactory {
	
	public abstract FakultetDataAccess getFakultetDataAccess();
	public abstract PredmetDataAccess getPredmetDataAccess();
	public abstract StudijskiProgramDataAccess getStudijskiProgramDataAccess();
	public abstract PredmetNaStudijskomProgramuDataAccess getPredmetNaStudijskomProgramuDataAccess();
	public abstract IzvestajiDataAccess getIzvestajiDataAccess();

	public static DataAccessFactory getFactory(DataAccessFactoryType type) {
		if (DataAccessFactoryType.MySQL.equals(type)) {
			return new MySQLDataAccessFactory();
		}
		throw new IllegalArgumentException();
	}
	
}

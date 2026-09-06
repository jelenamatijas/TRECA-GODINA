package org.unibl.etf.bp.uniis.data.mysql;

import org.unibl.etf.bp.uniis.data.DataAccessFactory;
import org.unibl.etf.bp.uniis.data.FakultetDataAccess;
import org.unibl.etf.bp.uniis.data.IzvestajiDataAccess;
import org.unibl.etf.bp.uniis.data.PredmetDataAccess;
import org.unibl.etf.bp.uniis.data.PredmetNaStudijskomProgramuDataAccess;
import org.unibl.etf.bp.uniis.data.StudijskiProgramDataAccess;

public class MySQLDataAccessFactory extends DataAccessFactory {

	@Override
	public FakultetDataAccess getFakultetDataAccess() {
		return new FakultetDataAccessImpl();
	}

	@Override
	public PredmetDataAccess getPredmetDataAccess() {
		return new PredmetDataAccessImpl();
	}

	@Override
	public StudijskiProgramDataAccess getStudijskiProgramDataAccess() {
		return new StudijskiProgramDataAccessImpl();
	}

	@Override
	public PredmetNaStudijskomProgramuDataAccess getPredmetNaStudijskomProgramuDataAccess() {
		return new PredmetNaStudijskomProgramuDataAccessImpl();
	}

	@Override
	public IzvestajiDataAccess getIzvestajiDataAccess() {
		return new IzvestajiDataAccessImpl();
	}

}

package org.unibl.etf.bp.uniis.data;

import java.util.List;

import org.unibl.etf.bp.uniis.entity.PredmetNaStudijskomProgramu;

public interface PredmetNaStudijskomProgramuDataAccess {
	
	List<PredmetNaStudijskomProgramu> predmetiNaSP(int idSP);
	boolean dodajPredmetNaSP(PredmetNaStudijskomProgramu predmetNaSP);
	boolean azurirajPredmetNaSP(PredmetNaStudijskomProgramu predmetNaSP);
	boolean obrisiPredmetNaSP(int idPredmeta, int idSP);
	
}

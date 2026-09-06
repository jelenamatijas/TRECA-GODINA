package org.unibl.etf.bp.uniis.data;

import java.util.List;

import org.unibl.etf.bp.uniis.entity.StudijskiProgram;

public interface StudijskiProgramDataAccess {
	
	StudijskiProgram studijskiProgram(int idSP);
	List<StudijskiProgram> studijskiProgrami(String nazivSP,
			Byte ciklus, String nazivFakulteta);
	boolean dodajStudijskiProgram(StudijskiProgram studijskiProgram);
	boolean azurirajStudijskiProgram(StudijskiProgram studijskiProgram);
	boolean obrisiStudijskiProgram(int idSP);
	
}

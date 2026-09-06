package org.unibl.etf.bp.uniis.data;

import java.util.List;

import org.unibl.etf.bp.uniis.entity.Fakultet;

public interface FakultetDataAccess {
	
	List<Fakultet> fakulteti(String nazivFakulteta);
	boolean dodajFakultet(Fakultet fakultet);
	boolean azurirajFakultet(Fakultet fakultet);
	boolean obrisiFakultet(String nazivFakulteta);
	
}

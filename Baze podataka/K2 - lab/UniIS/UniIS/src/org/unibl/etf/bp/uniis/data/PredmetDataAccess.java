package org.unibl.etf.bp.uniis.data;

import java.util.List;

import org.unibl.etf.bp.uniis.entity.Predmet;

public interface PredmetDataAccess {
	
	Predmet predmet(int idPredmeta);
	List<Predmet> predmeti(String nazivPredmeta, String nazivFakulteta);
	boolean dodajPredmet(Predmet predmet);
	boolean azurirajPredmet(Predmet predmet);
	boolean obrisiPredmet(int idPredmeta);
	
}

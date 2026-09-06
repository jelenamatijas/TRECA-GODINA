package org.unibl.etf.bp.uniis.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.unibl.etf.bp.uniis.entity.Predmet;

@SuppressWarnings("serial")
public class PredmetTableModel extends AbstractTableModel {
	
	private List<Predmet> podaci;
	String[] kolone = new String[] { "Identifikator", "Naziv predmeta", "ECTS",
			"Matični fakultet" };

	public PredmetTableModel(List<Predmet> podaci) {
		setPodaci(podaci);
	}

	public void setPodaci(List<Predmet> podaci) {
		this.podaci = podaci;
	}

	public Predmet getPredmetAtRow(int rowIndex) {
		return podaci.get(rowIndex);
	}

	@Override
	public int getColumnCount() {
		return kolone.length;
	}

	@Override
	public String getColumnName(int column) {
		return kolone[column];
	}

	@Override
	public int getRowCount() {
		return podaci.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Predmet red = podaci.get(rowIndex);
		if (columnIndex == 0)
			return red.getIdPredmeta();
		else if (columnIndex == 1)
			return red.getNazivPredmeta();
		else if (columnIndex == 2)
			return red.getEcts();
		else if (columnIndex == 3)
			return red.getFakultet();
		else
			return null;
	}
	
}

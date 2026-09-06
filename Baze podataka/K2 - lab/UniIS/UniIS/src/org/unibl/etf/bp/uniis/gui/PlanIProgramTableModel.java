package org.unibl.etf.bp.uniis.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.unibl.etf.bp.uniis.entity.PredmetNaStudijskomProgramu;

@SuppressWarnings("serial")
public class PlanIProgramTableModel extends AbstractTableModel {
	
	private List<PredmetNaStudijskomProgramu> podaci;
	String[] kolone = new String[] { "Identifikator predmeta",
			"Naziv predmeta", "Semestar", "Tip" };

	public PlanIProgramTableModel(List<PredmetNaStudijskomProgramu> podaci) {
		setPodaci(podaci);
	}

	public void setPodaci(List<PredmetNaStudijskomProgramu> podaci) {
		this.podaci = podaci;
	}

	public PredmetNaStudijskomProgramu getPredmetNaStudijskomProgramuAtRow(
			int rowIndex) {
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
		PredmetNaStudijskomProgramu red = podaci.get(rowIndex);
		if (columnIndex == 0)
			return red.getPredmet().getIdPredmeta();
		else if (columnIndex == 1)
			return red.getPredmet().getNazivPredmeta();
		else if (columnIndex == 2)
			return red.getSemestar();
		else if (columnIndex == 3)
			return red.getTipPredmeta();
		else
			return null;
	}

}

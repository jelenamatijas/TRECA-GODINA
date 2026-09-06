package org.unibl.etf.bp.uniis.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.unibl.etf.bp.uniis.entity.StudijskiProgram;

@SuppressWarnings("serial")
public class StudijskiProgramTableModel extends AbstractTableModel {
	
	private List<StudijskiProgram> podaci;
	String[] kolone = new String[] { "Identifikator",
			"Naziv studijskog programa", "Ciklus", "Trajanje", "Ukupno ECTS",
			"Zvanje", "Fakultet" };

	public StudijskiProgramTableModel(List<StudijskiProgram> podaci) {
		setPodaci(podaci);
	}

	public void setPodaci(List<StudijskiProgram> podaci) {
		this.podaci = podaci;
	}

	public StudijskiProgram getStudijskiProgramAtRow(int rowIndex) {
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
		StudijskiProgram red = podaci.get(rowIndex);
		if (columnIndex == 0)
			return red.getIdSP();
		else if (columnIndex == 1)
			return red.getNazivSP();
		else if (columnIndex == 2)
			return red.getCiklus();
		else if (columnIndex == 3)
			return red.getTrajanje();
		else if (columnIndex == 4)
			return red.getUkupanBrojEcts();
		else if (columnIndex == 5)
			return red.getZvanje();
		else if (columnIndex == 6)
			return red.getFakultet();
		else
			return null;
	}
	
}

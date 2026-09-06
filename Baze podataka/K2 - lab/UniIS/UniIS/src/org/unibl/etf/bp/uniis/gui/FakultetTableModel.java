package org.unibl.etf.bp.uniis.gui;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.unibl.etf.bp.uniis.entity.Fakultet;

@SuppressWarnings("serial")
public class FakultetTableModel extends AbstractTableModel {
	
	List<Fakultet> podaci;
	String[] kolone = new String[] { "Naziv fakulteta", "Adresa" };

	public FakultetTableModel(List<Fakultet> podaci) {
		setPodaci(podaci);
	}

	public void setPodaci(List<Fakultet> podaci) {
		this.podaci = podaci;
	}

	public Fakultet getFakultetAtRow(int rowIndex) {
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
		Fakultet red = podaci.get(rowIndex);
		if (columnIndex == 0)
			return red.getNazivFakulteta();
		else if (columnIndex == 1)
			return red.getAdresa();
		else
			return null;
	}
	
}

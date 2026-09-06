package org.unibl.etf.bp.uniis.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StudijskiProgram implements Serializable {
	
	private int idSP;
	private String nazivSP;
	private byte ciklus;
	private byte trajanje;
	private short ukupanBrojEcts;
	private String zvanje;
	private Fakultet fakultet;

	public StudijskiProgram() {
	}

	public StudijskiProgram(int idSP, String nazivSP, byte ciklus,
			byte trajanje, short ukupanBrojEcts, String zvanje,
			Fakultet fakultet) {
		super();
		this.idSP = idSP;
		this.nazivSP = nazivSP;
		this.ciklus = ciklus;
		this.trajanje = trajanje;
		this.ukupanBrojEcts = ukupanBrojEcts;
		this.zvanje = zvanje;
		this.fakultet = fakultet;
	}

	public int getIdSP() {
		return idSP;
	}

	public void setIdSP(int idSP) {
		this.idSP = idSP;
	}

	public String getNazivSP() {
		return nazivSP;
	}

	public void setNazivSP(String nazivSP) {
		this.nazivSP = nazivSP;
	}

	public byte getCiklus() {
		return ciklus;
	}

	public void setCiklus(byte ciklus) {
		this.ciklus = ciklus;
	}

	public byte getTrajanje() {
		return trajanje;
	}

	public void setTrajanje(byte trajanje) {
		this.trajanje = trajanje;
	}

	public short getUkupanBrojEcts() {
		return ukupanBrojEcts;
	}

	public void setUkupanBrojEcts(short ukupanBrojEcts) {
		this.ukupanBrojEcts = ukupanBrojEcts;
	}

	public String getZvanje() {
		return zvanje;
	}

	public void setZvanje(String zvanje) {
		this.zvanje = zvanje;
	}

	public Fakultet getFakultet() {
		return fakultet;
	}

	public void setFakultet(Fakultet fakultet) {
		this.fakultet = fakultet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idSP;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudijskiProgram other = (StudijskiProgram) obj;
		if (idSP != other.idSP)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nazivSP + " - " + ciklus;
	}
	
}

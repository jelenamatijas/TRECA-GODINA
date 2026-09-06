package org.unibl.etf.bp.uniis.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Predmet implements Serializable {
	
	private int idPredmeta;
	private String nazivPredmeta;
	private byte ects;
	private Fakultet fakultet;

	public Predmet() {
	}

	public Predmet(int idPredmeta, String nazivPredmeta, byte ects,
			Fakultet fakultet) {
		super();
		this.idPredmeta = idPredmeta;
		this.nazivPredmeta = nazivPredmeta;
		this.ects = ects;
		this.fakultet = fakultet;
	}

	public int getIdPredmeta() {
		return idPredmeta;
	}

	public void setIdPredmeta(int idPredmeta) {
		this.idPredmeta = idPredmeta;
	}

	public String getNazivPredmeta() {
		return nazivPredmeta;
	}

	public void setNazivPredmeta(String nazivPredmeta) {
		this.nazivPredmeta = nazivPredmeta;
	}

	public byte getEcts() {
		return ects;
	}

	public void setEcts(byte ects) {
		this.ects = ects;
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
		result = prime * result + idPredmeta;
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
		Predmet other = (Predmet) obj;
		if (idPredmeta != other.idPredmeta)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nazivPredmeta;
	}
	
}

package org.unibl.etf.bp.uniis.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Fakultet implements Serializable {
	
	private String nazivFakulteta;
	private String adresa;

	public Fakultet() {
	}

	public Fakultet(String nazivFakulteta, String adresa) {
		super();
		this.nazivFakulteta = nazivFakulteta;
		this.adresa = adresa;
	}

	public String getNazivFakulteta() {
		return nazivFakulteta;
	}

	public void setNazivFakulteta(String nazivFakulteta) {
		this.nazivFakulteta = nazivFakulteta;
	}

	public String getAdresa() {
		return adresa;
	}

	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nazivFakulteta == null) ? 0 : nazivFakulteta.hashCode());
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
		Fakultet other = (Fakultet) obj;
		if (nazivFakulteta == null) {
			if (other.nazivFakulteta != null)
				return false;
		} else if (!nazivFakulteta.equals(other.nazivFakulteta))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nazivFakulteta;
	}
	
}

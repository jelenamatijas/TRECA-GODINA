package org.unibl.etf.bp.uniis.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PredmetNaStudijskomProgramu implements Serializable {
	
	private Predmet predmet;
	private StudijskiProgram studijskiProgram;
	private byte semestar;
	private String tipPredmeta;

	public PredmetNaStudijskomProgramu() {
	}

	public PredmetNaStudijskomProgramu(Predmet predmet,
			StudijskiProgram studijskiProgram, byte semestar,
			String tipPredmeta) {
		super();
		this.predmet = predmet;
		this.studijskiProgram = studijskiProgram;
		this.semestar = semestar;
		this.tipPredmeta = tipPredmeta;
	}

	public Predmet getPredmet() {
		return predmet;
	}

	public void setPredmet(Predmet predmet) {
		this.predmet = predmet;
	}

	public StudijskiProgram getStudijskiProgram() {
		return studijskiProgram;
	}

	public void setStudijskiProgram(StudijskiProgram studijskiProgram) {
		this.studijskiProgram = studijskiProgram;
	}

	public byte getSemestar() {
		return semestar;
	}

	public void setSemestar(byte semestar) {
		this.semestar = semestar;
	}

	public String getTipPredmeta() {
		return tipPredmeta;
	}

	public void setTipPredmeta(String tipPredmeta) {
		this.tipPredmeta = tipPredmeta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predmet == null) ? 0 : predmet.hashCode());
		result = prime
				* result
				+ ((studijskiProgram == null) ? 0 : studijskiProgram.hashCode());
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
		PredmetNaStudijskomProgramu other = (PredmetNaStudijskomProgramu) obj;
		if (predmet == null) {
			if (other.predmet != null)
				return false;
		} else if (!predmet.equals(other.predmet))
			return false;
		if (studijskiProgram == null) {
			if (other.studijskiProgram != null)
				return false;
		} else if (!studijskiProgram.equals(other.studijskiProgram))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return predmet + " (" + studijskiProgram + ")";
	}
	
}

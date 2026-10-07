package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Zaposleni {
    private final StringProperty jmb;
    private final StringProperty ime;
    private final StringProperty prezime;
    private final StringProperty telefon;
    private final StringProperty radnoMjesto;

    public Zaposleni(String jmb, String ime, String prezime, String telefon, String radnoMjesto) {
        this.jmb = new SimpleStringProperty(jmb);
        this.ime = new SimpleStringProperty(ime);
        this.prezime = new SimpleStringProperty(prezime);
        this.telefon = new SimpleStringProperty(telefon);
        this.radnoMjesto = new SimpleStringProperty(radnoMjesto);
    }

    public String getJmb() { return jmb.get(); }
    public StringProperty jmbProperty() { return jmb; }

    public String getIme() { return ime.get(); }
    public StringProperty imeProperty() { return ime; }

    public String getPrezime() { return prezime.get(); }
    public StringProperty prezimeProperty() { return prezime; }

    public String getToggleTelefon() { return telefon.get(); }
    public String getTelefon() { return telefon.get(); }
    public StringProperty telefonProperty() { return telefon; }

    public String getRadnoMjesto() { return radnoMjesto.get(); }
    public StringProperty radnoMjestoProperty() { return radnoMjesto; }
}
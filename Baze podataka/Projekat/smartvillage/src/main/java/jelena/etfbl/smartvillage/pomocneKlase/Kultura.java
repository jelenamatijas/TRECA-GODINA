package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Kultura {
    private final StringProperty naziv;
    private final StringProperty vrsta;

    public Kultura(String naziv, String vrsta) {
        this.naziv = new SimpleStringProperty(naziv);
        this.vrsta = new SimpleStringProperty(vrsta);
    }

    public String getNaziv() {
        return naziv.get();
    }

    public String getVrsta() {
        return vrsta.get();
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public void setVrsta(String vrsta) {
        this.vrsta.set(vrsta);
    }

    public StringProperty nazivProperty() {
        return naziv;
    }

    public StringProperty vrstaProperty() {
        return vrsta;
    }
}
package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Hub {
    private final StringProperty naziv;
    private final StringProperty opis;

    public Hub(String naziv, String opis) {
        this.naziv = new SimpleStringProperty(naziv);
        this.opis = new SimpleStringProperty(opis);
    }

    public String getNaziv() { return naziv.get(); }
    public StringProperty nazivProperty() { return naziv; }

    public String getOpis() { return opis.get(); }
    public StringProperty opisProperty() { return opis; }
}
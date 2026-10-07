package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class IoTUredjaj {
    private final StringProperty serijskiBroj;
    private final StringProperty opis;

    private IntegerProperty status;
    private StringProperty hubNaziv;
    private IntegerProperty parcelaId;
    private StringProperty parcelaNaziv;

    public IoTUredjaj(String serijskiBroj, String opis) {
        this.serijskiBroj = new SimpleStringProperty(serijskiBroj);
        this.opis = new SimpleStringProperty(opis);
    }

    public IoTUredjaj(String serijskiBroj, int status, String opis, String hubNaziv, int parcelaId, String parcelaNaziv) {
        this.serijskiBroj = new SimpleStringProperty(serijskiBroj);
        this.opis = new SimpleStringProperty(opis);
        this.status = new SimpleIntegerProperty(status);
        this.hubNaziv = new SimpleStringProperty(hubNaziv);
        this.parcelaId = new SimpleIntegerProperty(parcelaId);
        this.parcelaNaziv = new SimpleStringProperty(parcelaNaziv);
    }

    public String getSerijskiBroj() { return serijskiBroj.get(); }
    public StringProperty serijskiBrojProperty() { return serijskiBroj; }

    public String getOpis() { return opis.get(); }
    public StringProperty opisProperty() { return opis; }

    public int getStatus() { return status != null ? status.get() : 0; }
    public IntegerProperty statusProperty() { return status; }
    public String getStatusTekst() { return getStatus() == 1 ? "Aktivan" : "Neaktivan"; }

    public String getHubNaziv() { return hubNaziv != null ? hubNaziv.get() : ""; }
    public StringProperty hubNazivProperty() { return hubNaziv; }

    public int getParcelaId() { return parcelaId != null ? parcelaId.get() : 0; }
    public IntegerProperty parcelaIdProperty() { return parcelaId; }

    public String getParcelaNaziv() { return parcelaNaziv != null ? parcelaNaziv.get() : ""; }
    public StringProperty parcelaNazivProperty() { return parcelaNaziv; }
}
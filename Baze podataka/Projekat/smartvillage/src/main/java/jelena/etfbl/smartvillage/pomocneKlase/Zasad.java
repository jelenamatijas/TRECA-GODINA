package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Zasad {
    private final StringProperty kulturaNaziv;
    private final ObjectProperty<LocalDate> datumSadnje;
    private final IntegerProperty brojSadnica;

    private StringProperty vrstaKulture;
    private StringProperty lokacijaInfo;

    public Zasad(String kulturaNaziv, LocalDate datumSadnje, int brojSadnica) {
        this.kulturaNaziv = new SimpleStringProperty(kulturaNaziv);
        this.datumSadnje = new SimpleObjectProperty<>(datumSadnje);
        this.brojSadnica = new SimpleIntegerProperty(brojSadnica);
    }

    public Zasad(String kulturaNaziv, String vrstaKulture, LocalDate datumSadnje, int brojSadnica,
                 int idParcele, String parcelaNaziv, String plastenikSifra, String plastenikTip) {
        this.kulturaNaziv = new SimpleStringProperty(kulturaNaziv);
        this.vrstaKulture = new SimpleStringProperty(vrstaKulture);
        this.datumSadnje = new SimpleObjectProperty<>(datumSadnje);
        this.brojSadnica = new SimpleIntegerProperty(brojSadnica);

        if (plastenikSifra != null && !plastenikSifra.isEmpty()) {
            this.lokacijaInfo = new SimpleStringProperty("Plastenik: " + plastenikTip + " (" + plastenikSifra + ") na parc. " + parcelaNaziv);
        } else {
            this.lokacijaInfo = new SimpleStringProperty("Otvoreno polje: " + parcelaNaziv + " (ID: " + idParcele + ")");
        }
    }


    public String getKulturaNaziv() { return kulturaNaziv.get(); }
    public StringProperty kulturaNazivProperty() { return kulturaNaziv; }

    public String getKultura() { return kulturaNaziv.get(); }
    public StringProperty kulturaProperty() { return kulturaNaziv; }

    public LocalDate getDatumSadnje() { return datumSadnje.get(); }
    public ObjectProperty<LocalDate> datumSadnjeProperty() { return datumSadnje; }

    public int getBrojSadnica() { return brojSadnica.get(); }
    public IntegerProperty brojSadnicaProperty() { return brojSadnica; }

    public String getVrstaKulture() { return vrstaKulture != null ? vrstaKulture.get() : ""; }
    public StringProperty vrstaKultureProperty() { return vrstaKulture; }

    public String getLokacijaInfo() { return lokacijaInfo != null ? lokacijaInfo.get() : ""; }
    public StringProperty lokacijaInfoProperty() { return lokacijaInfo; }
}
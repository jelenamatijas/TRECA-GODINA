package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Prinos {
    private final IntegerProperty idPrinosa;
    private final ObjectProperty<LocalDate> datumBerbe;
    private final DoubleProperty kolicina;
    private final IntegerProperty kvalitetOcjena;
    private final StringProperty kultura;
    private final StringProperty parcelaNaziv;

    public Prinos(int idPrinosa, LocalDate datumBerbe, double kolicina, int kvalitetOcjena, String kultura, String parcelaNaziv) {
        this.idPrinosa = new SimpleIntegerProperty(idPrinosa);
        this.datumBerbe = new SimpleObjectProperty<>(datumBerbe);
        this.kolicina = new SimpleDoubleProperty(kolicina);
        this.kvalitetOcjena = new SimpleIntegerProperty(kvalitetOcjena);
        this.kultura = new SimpleStringProperty(kultura);
        this.parcelaNaziv = new SimpleStringProperty(parcelaNaziv);
    }

    public int getIdPrinosa() { return idPrinosa.get(); }
    public IntegerProperty idPrinosaProperty() { return idPrinosa; }

    public LocalDate getDatumBerbe() { return datumBerbe.get(); }
    public ObjectProperty<LocalDate> datumBerbeProperty() { return datumBerbe; }

    public double getKolicina() { return kolicina.get(); }
    public DoubleProperty kolicinaProperty() { return kolicina; }

    public int getKvalitetOcjena() { return kvalitetOcjena.get(); }
    public IntegerProperty kvalitetOcjenaProperty() { return kvalitetOcjena; }

    public String getKultura() { return kultura.get(); }
    public StringProperty kulturaProperty() { return kultura; }

    public String getParcelaNaziv() { return parcelaNaziv.get(); }
    public StringProperty parcelaNazivProperty() { return parcelaNaziv; }
}
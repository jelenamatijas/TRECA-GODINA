package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.*;

public class Plastenik {
    private final StringProperty sifra;
    private final StringProperty tip;
    private final DoubleProperty povrsina;
    private final StringProperty vrstaNavodnjavanja;
    private final StringProperty hubNaziv;

    public Plastenik(String sifra, String tip, double povrsina, String vrstaNavodnjavanja, String hubNaziv) {
        this.sifra = new SimpleStringProperty(sifra);
        this.tip = new SimpleStringProperty(tip);
        this.povrsina = new SimpleDoubleProperty(povrsina);
        this.vrstaNavodnjavanja = new SimpleStringProperty(vrstaNavodnjavanja);
        this.hubNaziv = new SimpleStringProperty(hubNaziv);
    }


    public String getSifra() { return sifra.get(); }
    public StringProperty sifraProperty() { return sifra; }

    public String getTip() { return tip.get(); }
    public StringProperty tipProperty() { return tip; }

    public double getPovrsina() { return povrsina.get(); }
    public DoubleProperty povrsinaProperty() { return povrsina; }

    public String getVrstaNavodnjavanja() { return vrstaNavodnjavanja.get(); }
    public StringProperty vrstaNavodnjavanjaProperty() { return vrstaNavodnjavanja; }

    public String getHubNaziv() { return hubNaziv.get(); }
    public StringProperty hubNazivProperty() { return hubNaziv; }
}
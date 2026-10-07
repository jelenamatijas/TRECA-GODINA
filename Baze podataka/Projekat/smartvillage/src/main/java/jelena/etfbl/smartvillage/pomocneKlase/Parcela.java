package jelena.etfbl.smartvillage.pomocneKlase;

import javafx.beans.property.*;

public class Parcela {
    private final IntegerProperty id;
    private final StringProperty naziv;
    private final DoubleProperty povrsina;
    private final IntegerProperty katastarskaOpstina;
    private final StringProperty vlasnikImePrezime;

    public Parcela(int id, String naziv, double povrsina, int katastarskaOpstina, String vlasnikImePrezime) {
        this.id = new SimpleIntegerProperty(id);
        this.naziv = new SimpleStringProperty(naziv);
        this.povrsina = new SimpleDoubleProperty(povrsina);
        this.katastarskaOpstina = new SimpleIntegerProperty(katastarskaOpstina);
        this.vlasnikImePrezime = new SimpleStringProperty(vlasnikImePrezime);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public String getNaziv() { return naziv.get(); }
    public StringProperty nazivProperty() { return naziv; }
    public double getPovrsina() { return povrsina.get(); }
    public DoubleProperty povrsinaProperty() { return povrsina; }
    public int getKatastarskaOpstina() { return katastarskaOpstina.get(); }
    public IntegerProperty katastarskaOpstinaProperty() { return katastarskaOpstina; }
    public String getVlasnikImePrezime() { return vlasnikImePrezime.get(); }
    public StringProperty vlasnikImePrezimeProperty() { return vlasnikImePrezime; }

    @Override
    public String toString() {
        return getNaziv() + " (ID: " + getId() + ", " + getPovrsina() + " m²)";
    }
}
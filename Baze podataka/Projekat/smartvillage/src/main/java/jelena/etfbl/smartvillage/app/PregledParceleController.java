package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PregledParceleController {

    @FXML private Label id_label;
    @FXML private Label naziv_label;
    @FXML private Label povrsina_label;
    @FXML private Label ko_label;

    @FXML private ListView<String> zasadi_listView;
    @FXML private ListView<String> uredjaji_listView;

    private int trenutniIdParcele;

    private List<ZasadObrisanData> zasadiZaBrisanje = new ArrayList<>();
    private List<String> uredjajiZaBrisanje = new ArrayList<>();

    public static class ZasadObrisanData {
        public String kulturaNaziv;
        public int parcelaId;
        public ZasadObrisanData(String kulturaNaziv, int parcelaId) {
            this.kulturaNaziv = kulturaNaziv;
            this.parcelaId = parcelaId;
        }
    }

    public List<ZasadObrisanData> getZasadiZaBrisanje() { return zasadiZaBrisanje; }
    public List<String> getUredjajiZaBrisanje() { return uredjajiZaBrisanje; }

    public void inicijalizujPodatke(int idParcele) {
        this.trenutniIdParcele = idParcele;
        učitajOsnovnePodatke(idParcele);
        učitajZasade(idParcele);
        učitajUredjaje(idParcele);
    }

    private void učitajOsnovnePodatke(int idParcele) {
        String query = "select ID_Parcele, Naziv, Povrsina, KatastarskaOpstina from parcela where ID_Parcele = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idParcele);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id_label.setText(String.valueOf(rs.getInt("ID_Parcele")));
                    naziv_label.setText(rs.getString("Naziv"));
                    povrsina_label.setText(rs.getDouble("Povrsina") + " ha");
                    ko_label.setText(rs.getString("KatastarskaOpstina"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void učitajZasade(int idParcele) {
        ObservableList<String> zasadi = FXCollections.observableArrayList();
        String query = "select KULTURA_Naziv, BrojSadnica, DatumSadnje from zasad where PARCELA_ID_Parcele = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idParcele);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    zasadi.add(rs.getString("KULTURA_Naziv") + " (" + rs.getInt("BrojSadnica") + " sadnica) - " + rs.getDate("DatumSadnje"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (zasadi.isEmpty()) zasadi.add("Nema aktivnih zasada na ovoj parceli.");
        zasadi_listView.setItems(zasadi);
    }

    private void učitajUredjaje(int idParcele) {
        ObservableList<String> uredjaji = FXCollections.observableArrayList();
        String query = "select SerijskiBroj, Opis, Status from IoTUredjaj where PARCELA_ID_Parcele = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idParcele);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String statusTxt = rs.getInt("Status") == 1 ? "Aktivan" : "Neaktivan";
                    uredjaji.add("S/N: " + rs.getString("SerijskiBroj") + " [" + statusTxt + "] - " + rs.getString("Opis"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        if (uredjaji.isEmpty()) uredjaji.add("Nema povezanih IoT uređaja.");
        uredjaji_listView.setItems(uredjaji);
    }

    @FXML
    private void obrisiZasad() {
        String selektovanaStavka = zasadi_listView.getSelectionModel().getSelectedItem();
        if (selektovanaStavka == null || selektovanaStavka.equals("Nema aktivnih zasada na ovoj parceli.")) return;

        String nazivKulture = selektovanaStavka.split("\\(")[0].trim();
        zasadiZaBrisanje.add(new ZasadObrisanData(nazivKulture, trenutniIdParcele));
        zasadi_listView.getItems().remove(selektovanaStavka);
    }

    @FXML
    private void obrisiUredjaj() {
        String selektovanaStavka = uredjaji_listView.getSelectionModel().getSelectedItem();
        if (selektovanaStavka == null || selektovanaStavka.equals("Nema povezanih IoT uređaja.")) return;

        String dioSaSerijskim = selektovanaStavka.split("\\[")[0];
        String serijskiBroj = dioSaSerijskim.replace("S/N:", "").trim();

        uredjajiZaBrisanje.add(serijskiBroj);
        uredjaji_listView.getItems().remove(selektovanaStavka);
    }


    @FXML
    private void potvrdiIzmjene() {
        ((Stage) id_label.getScene().getWindow()).close();
    }

    @FXML
    private void odustani() {
        zasadiZaBrisanje.clear();
        uredjajiZaBrisanje.clear();

        ((Stage) id_label.getScene().getWindow()).close();
    }
}
package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

public class IzmjeniZadrugaraController {

    @FXML private TextField jmb_txtField;
    @FXML private TextField ime_txtField;
    @FXML private TextField prezime_txtField;
    @FXML private TextField telefon_txtField;
    @FXML private CheckBox aktivan_checkBox;
    @FXML private ListView<String> zadruge_listView;
    @FXML private ListView<String> parcele_listView;

    private Runnable refreshCallback;
    private List<ParcelaData> parceleNaCekanju = new ArrayList<>();
    private List<Integer> parceleZaBrisanje = new ArrayList<>();
    private List<PregledParceleController.ZasadObrisanData> sviZasadiZaBrisanje = new ArrayList<>();
    private List<String> sviUredjajiZaBrisanje = new ArrayList<>();

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void InicijalizujPodatke(String jmb) {
        jmb_txtField.setText(jmb);
        jmb_txtField.setEditable(false);
        ucitajZadrugaraIzBaze(jmb);

        ucitajZadrugeZadrugara(jmb);
        ucitajParceleZadrugara(jmb);
    }

    private void ucitajZadrugaraIzBaze(String jmb) {
        String query = "select Ime, Prezime, Telefon, Status from zadrugar where JMB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, jmb);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ime_txtField.setText(rs.getString("Ime"));
                    prezime_txtField.setText(rs.getString("Prezime"));
                    telefon_txtField.setText(rs.getString("Telefon"));

                    int status = rs.getInt("Status");
                    aktivan_checkBox.setSelected(status == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Greška prilikom učitavanja podataka iz baze.");
        }
    }

    @FXML
    private void otvoriProzorZaNovuParcelu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/novaParcela.fxml"));
            Parent root = loader.load();

            NovaParcelaController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Dodaj novu parcelu");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(jmb_txtField.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();

            ParcelaData nova = controller.getKreiranaParcela();
            if (nova != null) {
                parceleNaCekanju.add(nova);
                parcele_listView.getItems().add("ID: " + nova.id + " - Katastarska opstina: " + nova.katastarskaOpstina + " (" + nova.povrsina + " ha) [NA ČEKANJU]");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void spasiIzmjene() {
        String jmb = jmb_txtField.getText();
        String ime = ime_txtField.getText().trim();
        String prezime = prezime_txtField.getText().trim();
        String telefon = telefon_txtField.getText().trim();
        int status = aktivan_checkBox.isSelected() ? 1 : 0;

        String updateZadrugar = "update zadrugar set Ime = ?, Prezime = ?, Telefon = ?, Status = ? where JMB = ?";
        String insertParcela = "insert into parcela (ID_Parcele, Naziv, Povrsina, KatastarskaOpstina, ZADRUGAR_JMB) values (?, ?, ?, ?, ?)";

        String deleteZasad = "delete from zasad where KULTURA_Naziv = ? and PARCELA_ID_Parcele = ?";
        String deleteUredjaj = "delete from IoTUredjaj where SerijskiBroj = ?";
        String deleteParcela = "delete from parcela where ID_Parcele = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(updateZadrugar)) {
                psUpdate.setString(1, ime);
                psUpdate.setString(2, prezime);
                psUpdate.setString(3, telefon);
                psUpdate.setInt(4, status);
                psUpdate.setString(5, jmb);
                psUpdate.executeUpdate();
            }

            if (!parceleNaCekanju.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertParcela)) {
                    for (ParcelaData p : parceleNaCekanju) {
                        psInsert.setInt(1, p.id);
                        psInsert.setString(2, p.naziv);
                        psInsert.setDouble(3, p.povrsina);
                        psInsert.setInt(4, p.katastarskaOpstina);
                        psInsert.setString(5, jmb);
                        psInsert.executeUpdate();
                    }
                }
            }

            if (!sviZasadiZaBrisanje.isEmpty()) {
                try (PreparedStatement psDelZasad = conn.prepareStatement(deleteZasad)) {
                    for (PregledParceleController.ZasadObrisanData z : sviZasadiZaBrisanje) {
                        psDelZasad.setString(1, z.kulturaNaziv);
                        psDelZasad.setInt(2, z.parcelaId);
                        psDelZasad.executeUpdate();
                    }
                }
            }

            if (!sviUredjajiZaBrisanje.isEmpty()) {
                try (PreparedStatement psDelUredjaj = conn.prepareStatement(deleteUredjaj)) {
                    for (String serijski : sviUredjajiZaBrisanje) {
                        psDelUredjaj.setString(1, serijski);
                        psDelUredjaj.executeUpdate();
                    }
                }
            }

            if (!parceleZaBrisanje.isEmpty()) {
                try (PreparedStatement psDelete = conn.prepareStatement(deleteParcela)) {
                    for (int id : parceleZaBrisanje) {
                        psDelete.setInt(1, id);
                        psDelete.executeUpdate();
                    }
                }
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Podaci uspješno sačuvani.");

            if (refreshCallback != null) refreshCallback.run();
            ((Stage) jmb_txtField.getScene().getWindow()).close();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }

            if ("45000".equals(e.getSQLState())) {
                prikaziPoruku(Alert.AlertType.WARNING, "Zabrana brisanja", e.getMessage());
                parceleZaBrisanje.clear();
                sviZasadiZaBrisanje.clear();
                sviUredjajiZaBrisanje.clear();
                ucitajParceleZadrugara(jmb);
            } else {
                prikaziPoruku(Alert.AlertType.ERROR, "Sistemska greška", "Došlo je do greške. Nijedna izmjena nije sačuvana.");
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML private void ponisti() {
        ((Stage) jmb_txtField.getScene().getWindow()).close();
    }

    private void prikaziPoruku(Alert.AlertType tip, String naslov, String sadrzaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }

    private void ucitajZadrugeZadrugara(String jmb) {
        ObservableList<String> zadruge = FXCollections.observableArrayList();

        String query = "{call pZadrugeDatogZadrugara(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, jmb);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    zadruge.add(rs.getString("Naziv") + " (JIB: " + rs.getString("JIB") + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            zadruge.add("GREŠKA pri učitavanju zadruga preko procedure!");
        }

        zadruge_listView.setItems(zadruge);
    }

    private void ucitajParceleZadrugara(String jmb) {
        ObservableList<String> parcele = FXCollections.observableArrayList();

        String query = "{call pParceleDatogZadrugara(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, jmb);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    parcele.add("ID: " + rs.getString("ID_Parcele") +
                            " - Katastarska opstina: " + rs.getString("KatastarskaOpstina") +
                            " (" + rs.getString("Povrsina") + " ha)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            parcele.add("GREŠKA pri učitavanju parcela preko procedure!");
        }

        parcele_listView.setItems(parcele);
    }

    @FXML
    private void obrisiParcelu() {
        String selektovanaStavka = parcele_listView.getSelectionModel().getSelectedItem();

        if (selektovanaStavka == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati parcelu koju želite obrisati.");
            return;
        }

        try {
            String[] dijelovi = selektovanaStavka.split("-");
            String idStr = dijelovi[0].replace("ID:", "").trim();
            int idParcele = Integer.parseInt(idStr);

            if (selektovanaStavka.contains("[NA ČEKANJU]")) {
                parceleNaCekanju.removeIf(p -> p.id == idParcele);
            } else {
                parceleZaBrisanje.add(idParcele);
            }

            parcele_listView.getItems().remove(selektovanaStavka);

        } catch (Exception e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće obraditi selektovanu parcelu.");
        }
    }

    @FXML
    private void pregledajParcelu() {
        String selektovanaStavka = parcele_listView.getSelectionModel().getSelectedItem();

        if (selektovanaStavka == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati parcelu za pregled.");
            return;
        }

        try {
            String[] dijelovi = selektovanaStavka.split("-");
            String idStr = dijelovi[0].replace("ID:", "").trim();
            int idParcele = Integer.parseInt(idStr);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledParcele.fxml"));
            Parent root = loader.load();

            PregledParceleController controller = loader.getController();
            controller.inicijalizujPodatke(idParcele);

            Stage stage = new Stage();
            stage.setTitle("Pregled parcele br. " + idParcele);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(jmb_txtField.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();

            sviZasadiZaBrisanje.addAll(controller.getZasadiZaBrisanje());
            sviUredjajiZaBrisanje.addAll(controller.getUredjajiZaBrisanje());

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće otvoriti prozor za pregled parcele.");
        }
    }

    public static class ParcelaData {
        public int id;
        public String naziv;
        public double povrsina;
        public int katastarskaOpstina;
        public String hubNaziv;
        public String hubZadrugaJib;

        public ParcelaData(int id, String naziv, double povrsina, int ko, String hubNaziv, String hubZadrugaJib) {
            this.id = id;
            this.naziv = naziv;
            this.povrsina = povrsina;
            this.katastarskaOpstina = ko;
            this.hubNaziv = hubNaziv;
            this.hubZadrugaJib = hubZadrugaJib;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | " + naziv + " (" + povrsina + " m2) - Hab: " + hubNaziv;
        }
    }
}
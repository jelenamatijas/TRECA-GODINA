package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Zaposleni;

import java.sql.*;

public class PregledZaposlenihController {

    @FXML private TableView<Zaposleni> zaposleni_tableView;
    @FXML private TableColumn<Zaposleni, String> jmb_col;
    @FXML private TableColumn<Zaposleni, String> ime_col;
    @FXML private TableColumn<Zaposleni, String> prezime_col;
    @FXML private TableColumn<Zaposleni, String> telefon_col;
    @FXML private TableColumn<Zaposleni, String> radnoMjesto_col;

    @FXML private TextField jmb_txtField;
    @FXML private TextField ime_txtField;
    @FXML private TextField prezime_txtField;
    @FXML private TextField telefon_txtField;
    @FXML private TextField radnoMjesto_txtField;

    @FXML private ComboBox<String> hub_cmbBox;

    private String trenutniJibZadruge;
    private ObservableList<Zaposleni> listaZaposlenih = FXCollections.observableArrayList();
    private ObservableList<String> listaHabova = FXCollections.observableArrayList();
    private boolean modIzmjene = false;

    public void postaviZadrugu(String jib) {
        this.trenutniJibZadruge = jib;
        ucitajZaposlene();
        ucitajHaboveZadruge();
    }

    @FXML
    private void initialize() {
        jmb_col.setCellValueFactory(cellData -> cellData.getValue().jmbProperty());
        ime_col.setCellValueFactory(cellData -> cellData.getValue().imeProperty());
        prezime_col.setCellValueFactory(cellData -> cellData.getValue().prezimeProperty());
        telefon_col.setCellValueFactory(cellData -> cellData.getValue().telefonProperty());
        radnoMjesto_col.setCellValueFactory(cellData -> cellData.getValue().radnoMjestoProperty());

        zaposleni_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                prikaziZaposlenog(newValue);
            }
        });
    }

    private void ucitajZaposlene() {
        listaZaposlenih.clear();
        String query = "select * from zaposleni where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaZaposlenih.add(new Zaposleni(
                            rs.getString("JMB"),
                            rs.getString("Ime"),
                            rs.getString("Prezime"),
                            rs.getString("Telefon"),
                            rs.getString("RadnoMjesto")
                    ));
                }
            }
            zaposleni_tableView.setItems(listaZaposlenih);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajHaboveZadruge() {
        listaHabova.clear();
        listaHabova.add("Nije raspoređen");

        String query = "select Naziv from hub where ZADRUGA_JIB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaHabova.add(rs.getString("Naziv"));
                }
            }
            hub_cmbBox.setItems(listaHabova);
            hub_cmbBox.setValue("Nije raspoređen");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void prikaziZaposlenog(Zaposleni zaposleni) {
        modIzmjene = true;
        jmb_txtField.setText(zaposleni.getJmb());
        ime_txtField.setText(zaposleni.getIme());
        prezime_txtField.setText(zaposleni.getPrezime());
        telefon_txtField.setText(zaposleni.getTelefon());
        radnoMjesto_txtField.setText(zaposleni.getRadnoMjesto());

        jmb_txtField.setEditable(false);
        jmb_txtField.setStyle("-fx-background-color: #e9e9e9;");
        ime_txtField.setEditable(false);
        ime_txtField.setStyle("-fx-background-color: #e9e9e9;");
        prezime_txtField.setEditable(false);
        prezime_txtField.setStyle("-fx-background-color: #e9e9e9;");


        String queryHub = "select HUB_Naziv from HUB_has_ZAPOSLENI where ZAPOSLENI_JMB = ? and HUB_ZADRUGA_JIB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryHub)) {
            ps.setString(1, zaposleni.getJmb());
            ps.setString(2, trenutniJibZadruge);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hub_cmbBox.setValue(rs.getString("HUB_Naziv"));
                } else {
                    hub_cmbBox.setValue("Nije raspoređen");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pripremiZaNovog() {
        modIzmjene = false;
        zaposleni_tableView.getSelectionModel().clearSelection();

        jmb_txtField.clear();
        ime_txtField.clear();
        prezime_txtField.clear();
        telefon_txtField.clear();
        radnoMjesto_txtField.clear();
        hub_cmbBox.setValue("Nije raspoređen");

        jmb_txtField.setEditable(true);
        jmb_txtField.setStyle("-fx-background-color: white;");
        ime_txtField.setEditable(true);
        ime_txtField.setStyle("-fx-background-color: white;");
        prezime_txtField.setEditable(true);
        prezime_txtField.setStyle("-fx-background-color: white;");
    }

    @FXML
    private void sacuvajIzmjene() {
        String jmb = jmb_txtField.getText().trim();
        String ime = ime_txtField.getText().trim();
        String prezime = prezime_txtField.getText().trim();
        String telefon = telefon_txtField.getText().trim();
        String radnoMjesto = radnoMjesto_txtField.getText().trim();
        String odabraniHub = hub_cmbBox.getValue();

        if (jmb.isEmpty() || ime.isEmpty() || prezime.isEmpty() || telefon.isEmpty()) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Sva osnovna polja moraju biti popunjena.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (modIzmjene) {
                String updateQuery = "update zaposleni set Telefon = ?, RadnoMjesto = ? where JMB = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
                    ps.setString(1, telefon);
                    ps.setString(2, radnoMjesto);
                    ps.setString(3, jmb);
                    ps.executeUpdate();
                }
            } else {
                String insertQuery = "insert into zaposleni (JMB, Ime, Prezime, Telefon, RadnoMjesto, ZADRUGA_JIB) values (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                    ps.setString(1, jmb);
                    ps.setString(2, ime);
                    ps.setString(3, prezime);
                    ps.setString(4, telefon);
                    ps.setString(5, radnoMjesto);
                    ps.setString(6, trenutniJibZadruge);
                    ps.executeUpdate();
                }
            }


            String deleteHubVeza = "delete from HUB_has_ZAPOSLENI where ZAPOSLENI_JMB = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteHubVeza)) {
                ps.setString(1, jmb);
                ps.executeUpdate();
            }

            if (odabraniHub != null && !odabraniHub.equals("Nije raspoređen")) {
                String insertHubVeza = "insert into HUB_has_ZAPOSLENI (HUB_Naziv, HUB_ZADRUGA_JIB, ZAPOSLENI_JMB) values (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertHubVeza)) {
                    ps.setString(1, odabraniHub);
                    ps.setString(2, trenutniJibZadruge);
                    ps.setString(3, jmb);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Podaci o zaposlenom i rasporedu u HUB su uspješno sačuvani.");

            ucitajZaposlene();
            pripremiZaNovog();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Sistemska greška pri upisu u bazu podataka.");
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML
    private void obrisiZaposlenog() {
        Zaposleni selektovani = zaposleni_tableView.getSelectionModel().getSelectedItem();
        if (selektovani == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati zaposlenog iz tabele kojeg želite obrisati.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteHubVeza = "delete from HUB_has_ZAPOSLENI where ZAPOSLENI_JMB = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteHubVeza)) {
                ps.setString(1, selektovani.getJmb());
                ps.executeUpdate();
            }

            String queryZaposleni = "delete from zaposleni where JMB = ?";
            try (PreparedStatement ps = conn.prepareStatement(queryZaposleni)) {
                ps.setString(1, selektovani.getJmb());
                ps.executeUpdate();
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Zaposleni je uspješno uklonjen iz baze i izbrisan sa svih habova.");
            ucitajZaposlene();
            pripremiZaNovog();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće obrisati zaposlenog.");
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) jmb_txtField.getScene().getWindow();
        stage.close();
    }

    private void prikaziPoruku(Alert.AlertType tip, String naslov, String sadrzaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }
}
package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Plastenik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PregledPlastenikaController {

    @FXML private TableView<Plastenik> plastenici_tableView;
    @FXML private TableColumn<Plastenik, String> sifra_col;
    @FXML private TableColumn<Plastenik, String> tip_col;
    @FXML private TableColumn<Plastenik, Double> povrsina_col;
    @FXML private TableColumn<Plastenik, String> navodnjavanje_col;
    @FXML private TableColumn<Plastenik, String> hub_col;

    @FXML private TextField sifra_txtField;
    @FXML private TextField tip_txtField;
    @FXML private TextField povrsina_txtField;
    @FXML private TextField navodnjavanje_txtField;
    @FXML private ComboBox<String> hub_comboBox;

    private String trenutniJib;
    private ObservableList<Plastenik> listaPlastenika = FXCollections.observableArrayList();
    private ObservableList<String> listaHabova = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJib = jib;
        ucitajPlastenike();
        ucitajHaboveZadruge();
    }

    @FXML
    private void initialize() {
        sifra_col.setCellValueFactory(cellData -> cellData.getValue().sifraProperty());
        tip_col.setCellValueFactory(cellData -> cellData.getValue().tipProperty());
        povrsina_col.setCellValueFactory(cellData -> cellData.getValue().povrsinaProperty().asObject());
        navodnjavanje_col.setCellValueFactory(cellData -> cellData.getValue().vrstaNavodnjavanjaProperty());
        hub_col.setCellValueFactory(cellData -> cellData.getValue().hubNazivProperty());
    }

    private void ucitajPlastenike() {
        listaPlastenika.clear();
        String query = "select Sifra, Tip, Povrsina, VrstaNavodnjavanja, HUB_Naziv from vPregledPlastenikaZadruge where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, trenutniJib);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaPlastenika.add(new Plastenik(
                            rs.getString("Sifra"),
                            rs.getString("Tip"),
                            rs.getDouble("Povrsina"),
                            rs.getString("VrstaNavodnjavanja"),
                            rs.getString("HUB_Naziv")
                    ));
                }
            }
            plastenici_tableView.setItems(listaPlastenika);
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće učitati plastenike.");
        }
    }

    // Puni ComboBox nazivima habova koji pripadaju trenutnoj zadruzi
    private void ucitajHaboveZadruge() {
        listaHabova.clear();
        String query = "select Naziv from hub where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, trenutniJib);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaHabova.add(rs.getString("Naziv"));
                }
            }
            hub_comboBox.setItems(listaHabova);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dodajNoviPlastenik() {
        String sifra = sifra_txtField.getText().trim();
        String tip = tip_txtField.getText().trim();
        String povrsinaStr = povrsina_txtField.getText().trim();
        String navodnjavanje = navodnjavanje_txtField.getText().trim();
        String izabraniHub = hub_comboBox.getValue();

        if (sifra.isEmpty() || tip.isEmpty() || povrsinaStr.isEmpty() || navodnjavanje.isEmpty() || izabraniHub == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Sva polja moraju biti popunjena, uključujući i odabir HAB-a.");
            return;
        }

        double povrsina;
        try {
            povrsina = Double.parseDouble(povrsinaStr);
        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Površina mora biti brojčana vrijednost (npr. 150.50).");
            return;
        }

        String insertQuery = "insert into plastenik (Sifra, Tip, Povrsina, VrstaNavodnjavanja, HUB_Naziv, HUB_ZADRUGA_JIB) values (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

            ps.setString(1, sifra);
            ps.setString(2, tip);
            ps.setDouble(3, povrsina);
            ps.setString(4, navodnjavanje);
            ps.setString(5, izabraniHub);
            ps.setString(6, trenutniJib);

            ps.executeUpdate();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Novi plastenik uspješno kreiran i dodijeljen HAB-u " + izabraniHub);

            sifra_txtField.clear();
            tip_txtField.clear();
            povrsina_txtField.clear();
            navodnjavanje_txtField.clear();
            hub_comboBox.getSelectionModel().clearSelection();

            ucitajPlastenike();

        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Plastenik sa tom šifrom već postoji u bazi podataka.");
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) plastenici_tableView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void obrisiPlastenik() {
        Plastenik selektovaniPlastenik = plastenici_tableView.getSelectionModel().getSelectedItem();

        if (selektovaniPlastenik == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati plastenik iz tabele koji želite obrisati.");
            return;
        }

        Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION);
        potvrda.setTitle("Potvrda brisanja");
        potvrda.setHeaderText(null);
        potvrda.setContentText("Da li ste sigurni da želite obrisati plastenik sa šifrom: " + selektovaniPlastenik.getSifra() + "?");

        java.util.Optional<ButtonType> rezultat = potvrda.showAndWait();
        if (rezultat.isPresent() && rezultat.get() == ButtonType.OK) {

            String query = "delete from plastenik where Sifra = ? and HUB_ZADRUGA_JIB = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, selektovaniPlastenik.getSifra());
                ps.setString(2, trenutniJib);

                int obrisanoRedova = ps.executeUpdate();

                if (obrisanoRedova > 0) {
                    prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Plastenik je uspješno obrisan iz baze.");

                    listaPlastenika.remove(selektovaniPlastenik);
                } else {
                    prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Plastenik nije pronađen u bazi podataka.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                prikaziPoruku(Alert.AlertType.ERROR, "Greška pri brisanju",
                        "Nije moguće obrisati plastenik. Moguće je da su za njega vezani senzori, zasadi ili prinosi!");
            }
        }
    }

    private void prikaziPoruku(Alert.AlertType tip, String naslov, String sadrzaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }
}
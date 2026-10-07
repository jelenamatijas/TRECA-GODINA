package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Kultura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PregledKulturaController {

    @FXML private TableView<Kultura> kulture_tableView;
    @FXML private TableColumn<Kultura, String> naziv_col;
    @FXML private TableColumn<Kultura, String> vrsta_col;

    @FXML private TextField noviNaziv_txtField;
    @FXML private TextField novaVrsta_txtField;

    private String trenutniJib;
    private ObservableList<Kultura> listaKultura = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJib = jib;
        ucitajKultureZadruge();
    }

    @FXML
    private void initialize() {
        naziv_col.setCellValueFactory(cellData -> cellData.getValue().nazivProperty());
        vrsta_col.setCellValueFactory(cellData -> cellData.getValue().vrstaProperty());

        kulture_tableView.setItems(listaKultura);
    }

    private void ucitajKultureZadruge() {
        listaKultura.clear();
        String query = "select * from kultura";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String naziv = rs.getString("Naziv");
                    String vrsta = rs.getString("Vrsta");


                    listaKultura.add(new Kultura(naziv, vrsta));
                }
            }

            kulture_tableView.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "SQL Greška pri čitanju", e.getMessage());
        }
    }

    @FXML
    private void dodajNovuKulturu() {
        String naziv = noviNaziv_txtField.getText().trim();
        String vrsta = novaVrsta_txtField.getText().trim();

        if (naziv.isEmpty() || vrsta.isEmpty()) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate popuniti sva polja za unos nove kulture.");
            return;
        }

        String insertQuery = "insert into kultura (Naziv, Vrsta) values (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

            ps.setString(1, naziv);
            ps.setString(2, vrsta);
            ps.executeUpdate();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Nova kultura uspješno dodana u bazu podataka.");

            noviNaziv_txtField.clear();
            novaVrsta_txtField.clear();

            ucitajKultureZadruge();

        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška pri unosu", "Kultura sa ovim nazivom već postoji ili je baza odbila unos. Detalji: " + e.getMessage());
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) kulture_tableView.getScene().getWindow();
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
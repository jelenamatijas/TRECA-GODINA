package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.app.IzmjeniZadrugaraController.ParcelaData;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NovaParcelaController {

    @FXML private TextField id_txtField;
    @FXML private TextField naziv_txtField;
    @FXML private TextField povrsina_txtField;
    @FXML private TextField ko_txtField;
    @FXML private ComboBox<String> hub_cmbBox;

    private String zadrugarJmb;
    private String zadrugaJib;
    private ParcelaData kreiranaParcela = null;

    public void setPodaci(String jmb, String jib) {
        this.zadrugarJmb = jmb;
        this.zadrugaJib = jib;
        ucitajHabove();
    }

    public ParcelaData getKreiranaParcela() {
        return kreiranaParcela;
    }

    private void ucitajHabove() {
        ObservableList<String> habovi = FXCollections.observableArrayList();
        String query = "SELECT Naziv FROM HUB WHERE ZADRUGA_JIB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, zadrugaJib);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    habovi.add(rs.getString("Naziv"));
                }
            }
            hub_cmbBox.setItems(habovi);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void spasiParcelu() {
        try {
            int id = Integer.parseInt(id_txtField.getText().trim());
            String naziv = naziv_txtField.getText().trim();
            double povrsina = Double.parseDouble(povrsina_txtField.getText().trim());
            int ko = Integer.parseInt(ko_txtField.getText().trim());
            String izabraniHub = hub_cmbBox.getValue();

            if (izabraniHub == null) {
                prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate izabrati Hab kojem parcela pripada.");
                return;
            }

            kreiranaParcela = new ParcelaData(id, naziv, povrsina, ko, izabraniHub, zadrugaJib);

            ((Stage) id_txtField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.ERROR, "Greška u unosu", "Neispravan format brojeva.");
        }
    }

    @FXML
    private void ponisti() {
        zatvoriProzor();
    }

    private void zatvoriProzor() {
        ((Stage) id_txtField.getScene().getWindow()).close();
    }

    private void prikaziPoruku(Alert.AlertType tip, String naslov, String sadrzaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }
}
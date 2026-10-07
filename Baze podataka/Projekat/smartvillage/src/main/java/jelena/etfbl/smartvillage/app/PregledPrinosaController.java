package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Prinos;

import java.sql.*;
import java.time.LocalDate;

public class PregledPrinosaController {

    @FXML private TableView<Prinos> prinosi_tableView;
    @FXML private TableColumn<Prinos, Integer> id_col;
    @FXML private TableColumn<Prinos, String> kultura_col;
    @FXML private TableColumn<Prinos, String> parcela_col;
    @FXML private TableColumn<Prinos, LocalDate> datum_col;
    @FXML private TableColumn<Prinos, Double> kolicina_col;
    @FXML private TableColumn<Prinos, Integer> ocjena_col;

    private String trenutniJib;
    private ObservableList<Prinos> listaPrinosa = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJib = jib;
        ucitajPrinozeZadruge();
    }

    @FXML
    private void initialize() {
        id_col.setCellValueFactory(cellData -> cellData.getValue().idPrinosaProperty().asObject());
        kultura_col.setCellValueFactory(cellData -> cellData.getValue().kulturaProperty());
        parcela_col.setCellValueFactory(cellData -> cellData.getValue().parcelaNazivProperty());
        datum_col.setCellValueFactory(cellData -> cellData.getValue().datumBerbeProperty());
        kolicina_col.setCellValueFactory(cellData -> cellData.getValue().kolicinaProperty().asObject());
        ocjena_col.setCellValueFactory(cellData -> cellData.getValue().kvalitetOcjenaProperty().asObject());
    }

    private void ucitajPrinozeZadruge() {
        listaPrinosa.clear();
        String query = "{CALL pPregledPrinosaZadruge(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, trenutniJib);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("DatumBerbe");
                    listaPrinosa.add(new Prinos(
                            rs.getInt("ID_Prinosa"),
                            date != null ? date.toLocalDate() : null,
                            rs.getDouble("Kolicina"),
                            rs.getInt("KvalitetOcjena"),
                            rs.getString("Kultura"),
                            rs.getString("ParcelaNaziv")
                    ));
                }
            }
            prinosi_tableView.setItems(listaPrinosa);
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće učitati evidenciju prinosa preko procedure.");
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) prinosi_tableView.getScene().getWindow();
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
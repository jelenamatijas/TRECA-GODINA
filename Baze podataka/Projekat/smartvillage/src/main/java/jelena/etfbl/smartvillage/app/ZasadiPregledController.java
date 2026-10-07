package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Zasad;

import java.sql.*;
import java.time.LocalDate;

public class ZasadiPregledController {

    @FXML private TableView<Zasad> zasadi_tableView;
    @FXML private TableColumn<Zasad, String> kultura_col;
    @FXML private TableColumn<Zasad, String> vrsta_col;
    @FXML private TableColumn<Zasad, LocalDate> datum_col;
    @FXML private TableColumn<Zasad, Integer> sadnice_col;
    @FXML private TableColumn<Zasad, String> lokacija_col;

    private String trenutniJibZadruge;
    private ObservableList<Zasad> listaZasada = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJibZadruge = jib;
        ucitajSveZasade();
    }

    @FXML
    private void initialize() {
        kultura_col.setCellValueFactory(cellData -> cellData.getValue().kulturaProperty());
        vrsta_col.setCellValueFactory(cellData -> cellData.getValue().vrstaKultureProperty());
        datum_col.setCellValueFactory(cellData -> cellData.getValue().datumSadnjeProperty());
        sadnice_col.setCellValueFactory(cellData -> cellData.getValue().brojSadnicaProperty().asObject());
        lokacija_col.setCellValueFactory(cellData -> cellData.getValue().lokacijaInfoProperty());
    }

    private void ucitajSveZasade() {
        listaZasada.clear();
        String query = "select * from vPregledSvihZasadaZadruge where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, trenutniJibZadruge);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("DatumSadnje");

                    listaZasada.add(new Zasad(
                            rs.getString("Kultura"),
                            rs.getString("VrstaKulture"),
                            date != null ? date.toLocalDate() : null,
                            rs.getInt("BrojSadnica"),
                            rs.getInt("ID_Parcele"),
                            rs.getString("ParcelaNaziv"),
                            rs.getString("PlastenikSifra"),
                            rs.getString("PlastenikTip")
                    ));
                }
            }
            zasadi_tableView.setItems(listaZasada);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) zasadi_tableView.getScene().getWindow();
        stage.close();
    }
}
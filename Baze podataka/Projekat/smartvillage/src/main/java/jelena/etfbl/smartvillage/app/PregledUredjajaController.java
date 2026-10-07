package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.IoTUredjaj;
import jelena.etfbl.smartvillage.pomocneKlase.Parcela;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PregledUredjajaController {

    @FXML private TableView<IoTUredjaj> uredjaji_tableView;
    @FXML private TableColumn<IoTUredjaj, String> serijskiBroj_col;
    @FXML private TableColumn<IoTUredjaj, String> status_col;
    @FXML private TableColumn<IoTUredjaj, String> parcela_col;
    @FXML private TableColumn<IoTUredjaj, String> hub_col;
    @FXML private TableColumn<IoTUredjaj, String> opis_col;

    @FXML private TextField serijskiBroj_txtField;
    @FXML private CheckBox aktivan_checkBox;
    @FXML private ComboBox<Parcela> parcela_comboBox;
    @FXML private TextField opis_txtField;

    private String trenutniJib;
    private ObservableList<IoTUredjaj> listaUredjaja = FXCollections.observableArrayList();
    private ObservableList<Parcela> listaParcela = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJib = jib;
        ucitajUredjajeZadruge();
        ucitajParceleZadruge();
    }

    @FXML
    private void initialize() {
        serijskiBroj_col.setCellValueFactory(cellData -> cellData.getValue().serijskiBrojProperty());
        opis_col.setCellValueFactory(cellData -> cellData.getValue().opisProperty());
        status_col.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusTekst()));
        parcela_col.setCellValueFactory(cellData -> cellData.getValue().parcelaNazivProperty());
        hub_col.setCellValueFactory(cellData -> cellData.getValue().hubNazivProperty());

        uredjaji_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                popuniPoljaZaUredjaj(newValue);
            }
        });
    }

    private void popuniPoljaZaUredjaj(IoTUredjaj uredjaj) {
        serijskiBroj_txtField.setText(uredjaj.getSerijskiBroj());
        serijskiBroj_txtField.setEditable(false);
        opis_txtField.setText(uredjaj.getOpis());
        aktivan_checkBox.setSelected(uredjaj.getStatus() == 1);

        for (Parcela p : listaParcela) {
            if (p.getId() == uredjaj.getParcelaId()) {
                parcela_comboBox.setValue(p);
                break;
            }
        }
    }

    @FXML
    private void ocistiFormu() {
        serijskiBroj_txtField.clear();
        serijskiBroj_txtField.setEditable(true);
        opis_txtField.clear();
        aktivan_checkBox.setSelected(false);
        parcela_comboBox.getSelectionModel().clearSelection();
        uredjaji_tableView.getSelectionModel().clearSelection();
    }

    private void ucitajUredjajeZadruge() {
        listaUredjaja.clear();
        String query = "select SerijskiBroj, Status, Opis, HUB_Naziv, PARCELA_ID_Parcele, ParcelaNaziv from vPregledUredjajaZadruge where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, trenutniJib);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaUredjaja.add(new IoTUredjaj(
                            rs.getString("SerijskiBroj"),
                            rs.getInt("Status"),
                            rs.getString("Opis"),
                            rs.getString("HUB_Naziv"),
                            rs.getInt("PARCELA_ID_Parcele"),
                            rs.getString("ParcelaNaziv")
                    ));
                }
            }
            uredjaji_tableView.setItems(listaUredjaja);
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće učitati spisak uređaja.");
        }
    }

    private void ucitajParceleZadruge() {
        listaParcela.clear();
        String query = "select * from vPregledParcelaZadruge where ZADRUGA_JIB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJib);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String vlasnik = rs.getString("ZadrugarIme") + " " + rs.getString("ZadrugarPrezime");
                    listaParcela.add(new Parcela(
                            rs.getInt("ID_Parcele"),
                            rs.getString("ParcelaNaziv"),
                            rs.getDouble("Povrsina"),
                            rs.getInt("KatastarskaOpstina"),
                            vlasnik
                    ));
                }
            }
            parcela_comboBox.setItems(listaParcela);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void dodajNoviUredjaj() {
        String sBroj = serijskiBroj_txtField.getText().trim();
        String opis = opis_txtField.getText().trim();
        Parcela izabranaParcela = parcela_comboBox.getValue();
        int status = aktivan_checkBox.isSelected() ? 1 : 0;

        if (sBroj.isEmpty() || opis.isEmpty() || izabranaParcela == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate popuniti sva tekstualna polja i odabrati parcelu.");
            return;
        }

        boolean isEditMode = !serijskiBroj_txtField.isEditable();

        if (isEditMode) {
            String updateQuery = "update IoTUredjaj set Status = ?, Opis = ?, PARCELA_ID_Parcele = ?, " +
                    "HUB_Naziv = (select HUB_Naziv from parcela where ID_Parcele = ?) " +
                    "where SerijskiBroj = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateQuery)) {

                ps.setInt(1, status);
                ps.setString(2, opis);
                ps.setInt(3, izabranaParcela.getId());
                ps.setInt(4, izabranaParcela.getId());
                ps.setString(5, sBroj);

                ps.executeUpdate();
                prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Podaci o uređaju su uspješno ažurirani.");

                ocistiFormu();
                ucitajUredjajeZadruge();

            } catch (SQLException e) {
                e.printStackTrace();
                prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće ažurirati uređaj. Provjerite podatke.");
            }

        } else {
            String insertQuery = "insert into IoTUredjaj (SerijskiBroj, Status, Opis, HUB_Naziv, HUB_ZADRUGA_JIB, PARCELA_ID_Parcele) " +
                    "values (?, ?, ?, (select HUB_Naziv from parcela where ID_Parcele = ?), ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertQuery)) {

                ps.setString(1, sBroj);
                ps.setInt(2, status);
                ps.setString(3, opis);
                ps.setInt(4, izabranaParcela.getId());
                ps.setString(5, trenutniJib);
                ps.setInt(6, izabranaParcela.getId());

                ps.executeUpdate();
                prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Novi IoT uređaj je uspješno spojen na parcelu: " + izabranaParcela.getNaziv());

                ocistiFormu();
                ucitajUredjajeZadruge();

            } catch (SQLException e) {
                e.printStackTrace();
                prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Uređaj sa unesenim serijskim brojem već postoji u sistemu!");
            }
        }
    }

    @FXML
    private void obrisiUredjaj() {
        IoTUredjaj selektovani = uredjaji_tableView.getSelectionModel().getSelectedItem();

        if (selektovani == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Prvo izaberite uređaj iz tabele koji želite ukloniti.");
            return;
        }

        Alert potvrda = new Alert(Alert.AlertType.CONFIRMATION, "Da li ste sigurni da želite trajno ukloniti uređaj " + selektovani.getSerijskiBroj() + "?", ButtonType.YES, ButtonType.NO);
        potvrda.setHeaderText(null);
        potvrda.showAndWait();

        if (potvrda.getResult() == ButtonType.YES) {
            String deleteQuery = "update IoTUredjaj set PARCELA_ID_Parcele = NULL where SerijskiBroj = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(deleteQuery)) {

                ps.setString(1, selektovani.getSerijskiBroj());
                ps.executeUpdate();

                prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Uređaj je trajno uklonjen iz baze podataka.");
                ocistiFormu();
                ucitajUredjajeZadruge();

            } catch (SQLException e) {
                e.printStackTrace();
                prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće obrisati izabrani uređaj.");
            }
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) uredjaji_tableView.getScene().getWindow();
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
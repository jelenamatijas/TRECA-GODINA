package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.IoTUredjaj;
import jelena.etfbl.smartvillage.pomocneKlase.Parcela;
import jelena.etfbl.smartvillage.pomocneKlase.Zasad;

import java.sql.*;
import java.time.LocalDate;

public class PregledParcelaController {

    @FXML private TableView<Parcela> parcela_tableView;
    @FXML private TableColumn<Parcela, Integer> idParcela_col;
    @FXML private TableColumn<Parcela, String> nazivParcela_col;
    @FXML private TableColumn<Parcela, Double> povrsina_col;
    @FXML private TableColumn<Parcela, Integer> ko_col;

    @FXML private TextField vlasnik_txtField;

    @FXML private TableView<Zasad> zasad_tableView;
    @FXML private TableColumn<Zasad, String> kultura_col;
    @FXML private TableColumn<Zasad, LocalDate> datumSadnje_col;
    @FXML private TableColumn<Zasad, Integer> sadnice_col;

    // Unos prinosa
    @FXML private TextField kolicinaPrinosa_txtField;
    @FXML private TextField ocjenaPrinosa_txtField;

    // Novi zasad
    @FXML private ComboBox<String> kultura_cmbBox;
    @FXML private TextField brojSadnica_txtField;
    @FXML private DatePicker datumSadnje_datePicker;
    @FXML private TableView<IoTUredjaj> iot_tableView;
    @FXML private TableColumn<IoTUredjaj, String> serijskiBrojIot_col;
    @FXML private TableColumn<IoTUredjaj, String> opisIot_col;

    private ObservableList<IoTUredjaj> listaIotUredjaja = FXCollections.observableArrayList();

    private String trenutniJibZadruge;
    private ObservableList<Parcela> listaParcela = FXCollections.observableArrayList();
    private ObservableList<Zasad> listaZasada = FXCollections.observableArrayList();
    private ObservableList<String> sveKulture = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJibZadruge = jib;
        ucitajParcele();
        ucitajKulture();
    }

    @FXML
    private void initialize() {
        idParcela_col.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nazivParcela_col.setCellValueFactory(cellData -> cellData.getValue().nazivProperty());
        povrsina_col.setCellValueFactory(cellData -> cellData.getValue().povrsinaProperty().asObject());
        ko_col.setCellValueFactory(cellData -> cellData.getValue().katastarskaOpstinaProperty().asObject());

        kultura_col.setCellValueFactory(cellData -> cellData.getValue().kulturaNazivProperty());
        datumSadnje_col.setCellValueFactory(cellData -> cellData.getValue().datumSadnjeProperty());
        sadnice_col.setCellValueFactory(cellData -> cellData.getValue().brojSadnicaProperty().asObject());

        serijskiBrojIot_col.setCellValueFactory(cellData -> cellData.getValue().serijskiBrojProperty());
        opisIot_col.setCellValueFactory(cellData -> cellData.getValue().opisProperty());
        parcela_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                vlasnik_txtField.setText(newValue.getVlasnikImePrezime());
                ucitajZasadeZaParcelu(newValue.getId());
            } else {
                vlasnik_txtField.clear();
                zasad_tableView.getItems().clear();
            }
        });

        parcela_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                vlasnik_txtField.setText(newValue.getVlasnikImePrezime());
                ucitajZasadeZaParcelu(newValue.getId());
                ucitajIotUredjajeZaParcelu(newValue.getId());
            } else {
                vlasnik_txtField.clear();
                zasad_tableView.getItems().clear();
                iot_tableView.getItems().clear();
            }
        });
    }

    private void ucitajParcele() {
        listaParcela.clear();
        String query = "select * from vPregledParcelaZadruge where ZADRUGA_JIB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
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
            parcela_tableView.setItems(listaParcela);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajZasadeZaParcelu(int idParcele) {
        listaZasada.clear();
        String query = "select * from vPregledZasadaNaParceli where PARCELA_ID_Parcele = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idParcele);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("DatumSadnje");
                    listaZasada.add(new Zasad(
                            rs.getString("KULTURA_Naziv"),
                            date != null ? date.toLocalDate() : null,
                            rs.getInt("BrojSadnica")
                    ));
                }
            }
            zasad_tableView.setItems(listaZasada);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajKulture() {
        sveKulture.clear();
        String query = "select Naziv from KULTURA";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                sveKulture.add(rs.getString("Naziv"));
            }
            kultura_cmbBox.setItems(sveKulture);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajIotUredjajeZaParcelu(int idParcele) {
        listaIotUredjaja.clear();
        String query = "select SerijskiBroj, Opis from vPregledIotUredjajaNaParceli where PARCELA_ID_Parcele = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idParcele);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaIotUredjaja.add(new IoTUredjaj(
                            rs.getString("SerijskiBroj"),
                            rs.getString("Opis")
                    ));
                }
            }
            iot_tableView.setItems(listaIotUredjaja);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void evidentirajPrinos() {
        Parcela selektovanaParcela = parcela_tableView.getSelectionModel().getSelectedItem();
        Zasad selektovaniZasad = zasad_tableView.getSelectionModel().getSelectedItem();
        String kolicinaStr = kolicinaPrinosa_txtField.getText().trim();
        String ocjenaStr = ocjenaPrinosa_txtField.getText().trim();

        if (selektovaniZasad == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati zasad iz tabele da biste dodali prinos.");
            return;
        }
        if (kolicinaStr.isEmpty() || ocjenaStr.isEmpty()) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Unesite količinu i ocjenu kvaliteta.");
            return;
        }

        try {
            double kolicina = Double.parseDouble(kolicinaStr);
            int ocjena = Integer.parseInt(ocjenaStr);

            if (ocjena < 1 || ocjena > 5) {
                prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Ocjena kvaliteta mora biti između 1 i 5.");
                return;
            }

            String queryPrinos = "insert into prinos (DatumBerbe, Kolicina, KvalitetOcjena, ZASAD_KULTURA_Naziv, ZASAD_PARCELA_ID_Parcele) " +
                    "values (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(queryPrinos)) {

                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setDouble(2, kolicina);
                ps.setInt(3, ocjena);
                ps.setString(4, selektovaniZasad.getKulturaNaziv());
                ps.setInt(5, selektovanaParcela.getId());
                ps.executeUpdate();

                prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Prinos za ovaj zasad je uspješno evidentiran. Zasad ostaje aktivan.");

                kolicinaPrinosa_txtField.clear();
                ocjenaPrinosa_txtField.clear();
            }

        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Količina mora biti broj, a ocjena cijeli broj.");
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Danas je već unesen jedan prinos za ovu kulturu na ovoj parceli.");
        }
    }

    @FXML
    private void ukloniZasadSaParcele() {
        Parcela selektovanaParcela = parcela_tableView.getSelectionModel().getSelectedItem();
        Zasad selektovaniZasad = zasad_tableView.getSelectionModel().getSelectedItem();

        if (selektovanaParcela == null || selektovaniZasad == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati parcelu i zasad koji želite trajno obrisati.");
            return;
        }

        Alert konfirmacija = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmacija.setTitle("Potvrda brisanja zasada");
        konfirmacija.setHeaderText(null);
        konfirmacija.setContentText("Da li ste sigurni da želite trajno ukloniti ovaj zasad sa parcele?\n" +
                "Pažnja: Biće obrisani i svi evidentirani prinosi vezani za ovaj zasad!");

        if (konfirmacija.showAndWait().get() != ButtonType.OK) {
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            String obrisiPrinozeQuery = "delete from prinos where ZASAD_KULTURA_Naziv = ? and ZASAD_PARCELA_ID_Parcele = ?";
            try (PreparedStatement ps = conn.prepareStatement(obrisiPrinozeQuery)) {
                ps.setString(1, selektovaniZasad.getKulturaNaziv());
                ps.setInt(2, selektovanaParcela.getId());
                ps.executeUpdate();
            }

            String obrisiZasadQuery = "delete from zasad where KULTURA_Naziv = ? and PARCELA_ID_Parcele = ?";
            try (PreparedStatement ps = conn.prepareStatement(obrisiZasadQuery)) {
                ps.setString(1, selektovaniZasad.getKulturaNaziv());
                ps.setInt(2, selektovanaParcela.getId());
                ps.executeUpdate();
            }

            conn.commit();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Zasad i svi njegovi pripadajući prinosi su trajno obrisani.");

            ucitajZasadeZaParcelu(selektovanaParcela.getId());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Došlo je do greške prilikom brisanja zasada i prinosa iz baze podataka.");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void dodajNoviZasad() {
        Parcela selektovana = parcela_tableView.getSelectionModel().getSelectedItem();
        String kultura = kultura_cmbBox.getValue();
        String brojSadnicaStr = brojSadnica_txtField.getText().trim();
        LocalDate datum = datumSadnje_datePicker.getValue();

        if (selektovana == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate izabrati parcelu iz tabele.");
            return;
        }
        if (kultura == null || brojSadnicaStr.isEmpty() || datum == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Sva polja za novi zasad moraju biti popunjena.");
            return;
        }

        try {
            int brojSadnica = Integer.parseInt(brojSadnicaStr);
            String query = "insert into zasad (DatumSadnje, BrojSadnica, PARCELA_ID_Parcele, KULTURA_Naziv) values (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setDate(1, Date.valueOf(datum));
                ps.setInt(2, brojSadnica);
                ps.setInt(3, selektovana.getId());
                ps.setString(4, kultura);
                ps.executeUpdate();

                prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Novi zasad je uspješno dodan.");

                brojSadnica_txtField.clear();
                datumSadnje_datePicker.setValue(null);
                kultura_cmbBox.setValue(null);
                ucitajZasadeZaParcelu(selektovana.getId());
            }
        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Broj sadnica mora biti cijeli broj.");
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Ta kultura je već zasađena na ovoj parceli.");
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) vlasnik_txtField.getScene().getWindow();
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
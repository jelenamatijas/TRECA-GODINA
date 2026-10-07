package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KreirajZadruguController {
    @FXML TextField naziv_txtField;
    @FXML TextField jib_txtField;
    @FXML TextField adresa_txtFiled;
    @FXML TextField telefon_txtFiled;
    @FXML ComboBox<String> direktor_cmbBox;

    @FXML TextField jmb_txtFiled;
    @FXML TextField ime_txtFiled;
    @FXML TextField prezime_txtFiled;
    @FXML TextField dirTelefon_txtFiled;
    @FXML DatePicker datum_datePicker;
    @FXML TextField duzinaMandata_txtField;

    @FXML TableView<ZadrugarModel> zadrugari_tableView;
    @FXML TableColumn<ZadrugarModel, String> colJmbZadrugar;
    @FXML TableColumn<ZadrugarModel, String> colImeZadrugar;
    @FXML TableColumn<ZadrugarModel, String> colPrezimeZadrugar;
    @FXML TableColumn<ZadrugarModel, String> colTelefonZadrugar;
    @FXML TableColumn<ZadrugarModel, String> colNoviZadrugar;

    @FXML Button kreirajZadrugu_button;

    private ObservableList<ZadrugarModel> listaZadrugara = FXCollections.observableArrayList();

    @FXML private void initialize() {
        ucitajDirektore();

        direktor_cmbBox.getSelectionModel().selectedItemProperty().addListener((observable, old, newValue) -> {
            if (newValue != null && !newValue.equals("Nema raspoloživih direktora.")) {
                int pocetakJMB = newValue.indexOf("(") + 1;
                int krajJMB = newValue.indexOf(")");
                String jmb = newValue.substring(pocetakJMB, krajJMB);
                azuriraj(jmb);
            }
        });

        colJmbZadrugar.setCellValueFactory(new PropertyValueFactory<>("jmb"));
        colImeZadrugar.setCellValueFactory(new PropertyValueFactory<>("ime"));
        colPrezimeZadrugar.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        colTelefonZadrugar.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        colNoviZadrugar.setCellValueFactory(new PropertyValueFactory<>("statusNovogString"));

        zadrugari_tableView.setItems(listaZadrugara);
    }


    @FXML private void dodajPostojecegZadrugara() {
        List<String> postojeceOpcije = new ArrayList<>();
        String query = "select * from zadrugar";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String jmb = rs.getString("JMB");

                if (listaZadrugara.stream().noneMatch(z -> z.getJmb().equals(jmb))) {
                    postojeceOpcije.add(rs.getString("Ime") + " " + rs.getString("Prezime") + " " + rs.getString("Telefon")+ " (" + jmb + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće učitati zadrugare iz baze.");
            return;
        }

        if (postojeceOpcije.isEmpty()) {
            prikaziPoruku(Alert.AlertType.INFORMATION, "Info", "Nema dostupnih zadrugara za dodavanje.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(postojeceOpcije.get(0), postojeceOpcije);
        dialog.setTitle("Dodaj postojećeg zadrugara");
        dialog.setHeaderText("Odaberite zadrugara iz baze:");
        dialog.setContentText("Zadrugar:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(odabir -> {
            int pocetak = odabir.indexOf("(") + 1;
            int kraj = odabir.indexOf(")");
            String jmb = odabir.substring(pocetak, kraj);
            String imePrezime = odabir.substring(0, pocetak - 2);
            String[] split = imePrezime.split(" ", 3);
            String ime = split[0];
            String prezime = split.length > 1 ? split[1] : "";
            String telefon = split.length > 2 ? split[2] : "";

            listaZadrugara.add(new ZadrugarModel(jmb, ime, prezime, telefon, 1, false));
        });
    }

    @FXML
    private void kreirajNovogZadrugara() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/noviZadrugar.fxml"));
            Parent root = loader.load();

            NoviZadrugarController controller = loader.getController();
            controller.PostaviPostojeceZadrugare(listaZadrugara);

            Stage stage = new Stage();
            stage.setTitle("Kreiraj novog zadrugara i parcele");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.setScene(new Scene(root));

            stage.showAndWait();

            ZadrugarModel noviZadrugar = controller.getKreiraniZadrugar();
            if (noviZadrugar != null) {
                listaZadrugara.add(noviZadrugar);
            }

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće otvoriti prozor za novog zadrugara.");
        }
    }

    @FXML private void ukloniZadrugaraIzTabele() {
        ZadrugarModel selectedItem = zadrugari_tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            listaZadrugara.remove(selectedItem);
        } else {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Odaberite zadrugara iz tabele za uklanjanje.");
        }
    }


    @FXML private void kreiranjeZadruge() {
        String nazivZadruge = naziv_txtField.getText().trim();
        String jibZadruge = jib_txtField.getText().trim();
        String adresaZadruge = adresa_txtFiled.getText().trim();
        String telefonZadruge = telefon_txtFiled.getText().trim();

        String jmbDirektora = jmb_txtFiled.getText().trim();
        String imeDirektora = ime_txtFiled.getText().trim();
        String prezimeDirektora = prezime_txtFiled.getText().trim();
        String telDirektora = dirTelefon_txtFiled.getText().trim();
        LocalDate datumStupanja = datum_datePicker.getValue();
        String trajanjeMandataRaw = duzinaMandata_txtField.getText().trim();

        if (jibZadruge.isEmpty() || nazivZadruge.isEmpty() || jmbDirektora.isEmpty() || datumStupanja == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Molimo ispunite sva obavezna polja (JIB, Naziv zadruge, JMB i Datum stupanja).");
            return;
        }

        int trajanjeMandata = 0;
        try {
            trajanjeMandata = Integer.parseInt(trajanjeMandataRaw);
        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Trajanje mandata mora biti broj.");
            return;
        }

        Date sqlDatum = Date.valueOf(datumStupanja);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlProvjeraJIB = "select JIB from zadruga where JIB = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlProvjeraJIB)) {
                ps.setString(1, jibZadruge);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Zadruga sa JIB brojem " + jibZadruge + " već postoji!");
                        conn.rollback();
                        return;
                    }
                }
            }

            String sqlProvjeraJMB = "select JMB from direktor where JMB = ?";
            boolean direktorPostoji = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlProvjeraJMB)) {
                ps.setString(1, jmbDirektora);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        direktorPostoji = true;
                    }
                }
            }

            if (!direktorPostoji) {
                String sqlInsertDir = "insert into direktor (JMB, Ime, Prezime, Telefon, DatumStupanjaNaDuznost, TrajanjeMandata) values (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertDir)) {
                    ps.setString(1, jmbDirektora);
                    ps.setString(2, imeDirektora);
                    ps.setString(3, prezimeDirektora);
                    ps.setString(4, telDirektora);
                    ps.setDate(5, sqlDatum);
                    ps.setInt(6, trajanjeMandata);
                    ps.executeUpdate();
                }
            } else {
                String sqlUpdateDir = "update direktor set Ime = ?, Prezime = ?, Telefon = ?, DatumStupanjaNaDuznost = ?, TrajanjeMandata = ? where JMB = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDir)) {
                    ps.setString(1, imeDirektora);
                    ps.setString(2, prezimeDirektora);
                    ps.setString(3, telDirektora);
                    ps.setDate(4, sqlDatum);
                    ps.setInt(5, trajanjeMandata);
                    ps.setString(6, jmbDirektora);
                    ps.executeUpdate();
                }
            }

            String sqlInsertZadruga = "insert into zadruga (JIB, Naziv, Adresa, Telefon, Direktor_JMB, Aktivna) values (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertZadruga)) {
                ps.setString(1, jibZadruge);
                ps.setString(2, nazivZadruge);
                ps.setString(3, adresaZadruge);
                ps.setString(4, telefonZadruge);
                ps.setString(5, jmbDirektora);
                ps.setBoolean(6, true);
                ps.executeUpdate();
            }

            String sqlZadrugar = "insert into zadrugar (JMB, Ime, Prezime, Telefon, Status) values (?, ?, ?, ?, ?)";
            String sqlVeza = "insert into zadrugar_zadruga (ZADRUGAR_JMB, ZADRUGA_JIB) values (?, ?)";
            String sqlInsertParcela = "insert into parcela (ID_Parcele, Naziv, Povrsina, KatastarskaOpstina, ZADRUGAR_JMB, HUB_Naziv, HUB_ZADRUGA_JIB) values (?, ?, ?, ?, ?, ?, ?)";

            for (ZadrugarModel z : listaZadrugara) {
                if (z.isNovi()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlZadrugar)) {
                        ps.setString(1, z.getJmb());
                        ps.setString(2, z.getIme());
                        ps.setString(3, z.getPrezime());
                        ps.setString(4, z.getTelefon());
                        ps.setInt(5, z.getStatus());
                        ps.executeUpdate();
                    }

                    if (!z.getParcele().isEmpty()) {
                        try (PreparedStatement psParcela = conn.prepareStatement(sqlInsertParcela)) {
                            for (IzmjeniZadrugaraController.ParcelaData p : z.getParcele()) {
                                psParcela.setInt(1, p.id);
                                psParcela.setString(2, p.naziv);
                                psParcela.setDouble(3, p.povrsina);
                                psParcela.setInt(4, p.katastarskaOpstina);
                                psParcela.setString(5, z.getJmb());
                                psParcela.setString(6, p.hubNaziv);
                                psParcela.setString(7, p.hubZadrugaJib);
                                psParcela.executeUpdate();
                            }
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlVeza)) {
                    ps.setString(1, z.getJmb());
                    ps.setString(2, jibZadruge);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Zadruga, direktor i članovi su uspješno sačuvani!");
            ((Stage) naziv_txtField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            prikaziPoruku(Alert.AlertType.ERROR, "Sistemska greška", "Greška prilikom upisa u bazu podataka.");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

    private void ucitajDirektore() {
        ObservableList<String> direktori = FXCollections.observableArrayList();
        String query = "select * from vIstekliMandati";
        List<String> podaci = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet result = ps.executeQuery()) {
            while (result.next()) {
                if (podaci.isEmpty()) {
                    podaci.add(result.getString("Ime"));
                    podaci.add(result.getString("Prezime"));
                    podaci.add(result.getString("JMB"));
                    podaci.add(result.getString("Telefon"));
                    podaci.add(result.getString("DatumStupanjaNaDuznost"));
                    podaci.add(result.getString("TrajanjeMandata"));
                }
                String red = result.getString("Ime") + " " +
                        result.getString("Prezime") + " (" +
                        result.getString("JMB") + ")";
                direktori.add(red);
            }

            if (direktori.isEmpty()) {
                direktori.add("Nema raspoloživih direktora.");
            }

            direktor_cmbBox.setItems(direktori);
            if (!direktori.isEmpty()) {
                direktor_cmbBox.getSelectionModel().selectFirst();
                if(!podaci.isEmpty()) upisiPodatke(podaci);
                direktor_cmbBox.setVisibleRowCount(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GRESKA pri ucitavanju direktora pomocu pogleda iz baze!");
        }
    }

    private void upisiPodatke(List<String> upisi) {
        if(upisi.size() < 6) return;
        ime_txtFiled.setText(upisi.get(0));
        prezime_txtFiled.setText(upisi.get(1));
        jmb_txtFiled.setText(upisi.get(2));
        dirTelefon_txtFiled.setText(upisi.get(3));
        if(upisi.get(4) != null) datum_datePicker.setValue(LocalDate.parse(upisi.get(4)));
        duzinaMandata_txtField.setText(upisi.get(5));
    }

    private void azuriraj(String jmb) {
        String query = "select * from vIstekliMandati where JMB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, jmb);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    List<String> podaci = new ArrayList<>();
                    podaci.add(result.getString("Ime"));
                    podaci.add(result.getString("Prezime"));
                    podaci.add(result.getString("JMB"));
                    podaci.add(result.getString("Telefon"));
                    podaci.add(result.getString("DatumStupanjaNaDuznost"));
                    podaci.add(result.getString("TrajanjeMandata"));

                    upisiPodatke(podaci);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GRESKA pri ucitavanju direktora pomocu pogleda iz baze!");
        }
    }


    public static class ZadrugarModel {
        private String jmb;
        private String ime;
        private String prezime;
        private String telefon;
        private int status;
        private boolean novi;
        private List<IzmjeniZadrugaraController.ParcelaData> parcele = new ArrayList<>();

        public ZadrugarModel(String jmb, String ime, String prezime, String telefon, int status, boolean novi) {
            this.jmb = jmb;
            this.ime = ime;
            this.prezime = prezime;
            this.telefon = telefon;
            this.status = status;
            this.novi = novi;
        }

        public String getJmb() { return jmb; }
        public String getIme() { return ime; }
        public String getPrezime() { return prezime; }
        public String getTelefon() { return telefon; }
        public int getStatus() { return status; }
        public boolean isNovi() { return novi; }
        public String getStatusNovogString() { return novi ? "Da" : "Ne"; }

        public List<IzmjeniZadrugaraController.ParcelaData> getParcele() { return parcele; }
        public void setParcele(List<IzmjeniZadrugaraController.ParcelaData> parcele) { this.parcele = parcele; }
    }
}
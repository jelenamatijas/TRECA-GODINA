package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;

public class UrediZadruguController {

    @FXML private TextField naziv_txtField;
    @FXML private TextField jib_txtField;
    @FXML private TextField adresa_txtField;
    @FXML private TextField telefon_txtField;
    @FXML private CheckBox aktivna_checkBox;

    @FXML private Button odaberiNovog_btn;
    @FXML private Label izborDir_lbl;
    @FXML private ComboBox<String> direktor_cmbBox;

    @FXML private TextField jmb_txtField;
    @FXML private TextField ime_txtField;
    @FXML private TextField prezime_txtField;
    @FXML private TextField dirTelefon_txtField;
    @FXML private DatePicker datum_datePicker;
    @FXML private TextField duzinaMandata_txtField;

    private String selektovaniJib;
    private boolean modPromjeneDirektora = false;

    public void inicijalizujPodatke(String jib) {
        this.selektovaniJib = jib;

        ucitajPodatkeZadrugeITrenutnogDirektora();
    }

    @FXML
    private void initialize() {
        direktor_cmbBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.startsWith("Nema")) return;

            String jmb = parsirajJmb(newValue);
            popuniPoljaDirektoraIzBaze(jmb);
        });
    }

    private void ucitajPodatkeZadrugeITrenutnogDirektora() {
        String query = "select * from vPregledZadruge where JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, selektovaniJib);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    naziv_txtField.setText(rs.getString("Naziv"));
                    jib_txtField.setText(rs.getString("JIB"));
                    adresa_txtField.setText(rs.getString("Adresa"));
                    telefon_txtField.setText(rs.getString("Telefon"));
                    aktivna_checkBox.setSelected(rs.getByte("Aktivna") == 1);

                    jmb_txtField.setText(rs.getString("Direktor_JMB"));
                    ime_txtField.setText(rs.getString("Ime"));
                    prezime_txtField.setText(rs.getString("Prezime"));
                    dirTelefon_txtField.setText(rs.getString("DirTel"));

                    if (rs.getDate("DatumStupanjaNaDuznost") != null) {
                        datum_datePicker.setValue(rs.getDate("DatumStupanjaNaDuznost").toLocalDate());
                    }
                    duzinaMandata_txtField.setText(String.valueOf(rs.getInt("TrajanjeMandata")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //System.out.println("GRESKA: Nije moguće učitati podatke preko pogleda vPregledZadruge.");
        }
    }


    @FXML
    private void aktivirajOpcijuPromjeneDirektora() {
        modPromjeneDirektora = true;

        izborDir_lbl.setVisible(true);
        direktor_cmbBox.setVisible(true);
        odaberiNovog_btn.setDisable(true);

        jmb_txtField.setEditable(true);
        jmb_txtField.setStyle("-fx-background-color: white;");
        ime_txtField.setEditable(true);
        ime_txtField.setStyle("-fx-background-color: white;");
        prezime_txtField.setEditable(true);
        prezime_txtField.setStyle("-fx-background-color: white;");

        jmb_txtField.clear();
        ime_txtField.clear();
        prezime_txtField.clear();
        dirTelefon_txtField.clear();
        datum_datePicker.setValue(null);
        duzinaMandata_txtField.clear();

        ucitajSlobodneDirektoreIzBaze();
    }

    private void ucitajSlobodneDirektoreIzBaze() {
        ObservableList<String> slobodniDirektori = FXCollections.observableArrayList();

        String query = "select JMB, Ime, Prezime from direktor where JMB not in (select Direktor_JMB from zadruga)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                slobodniDirektori.add(rs.getString("Ime") + " " + rs.getString("Prezime") + " (" + rs.getString("JMB") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (slobodniDirektori.isEmpty()) {
            slobodniDirektori.add("Nema raspoloživih slobodnih direktora.");
        }
        direktor_cmbBox.setItems(slobodniDirektori);
    }

    private void popuniPoljaDirektoraIzBaze(String jmb) {
        String query = "select * from direktor where JMB = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, jmb);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jmb_txtField.setText(rs.getString("JMB"));
                    ime_txtField.setText(rs.getString("Ime"));
                    prezime_txtField.setText(rs.getString("Prezime"));
                    dirTelefon_txtField.setText(rs.getString("Telefon"));
                    if (rs.getDate("DatumStupanjaNaDuznost") != null) {
                        datum_datePicker.setValue(rs.getDate("DatumStupanjaNaDuznost").toLocalDate());
                    }
                    duzinaMandata_txtField.setText(String.valueOf(rs.getInt("TrajanjeMandata")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sacuvajIzmjene() {
        String adresa = adresa_txtField.getText().trim();
        String telefon = telefon_txtField.getText().trim();
        boolean aktivna = aktivna_checkBox.isSelected();

        String jmb = jmb_txtField.getText().trim();
        String ime = ime_txtField.getText().trim();
        String prezime = prezime_txtField.getText().trim();
        String dirTel = dirTelefon_txtField.getText().trim();
        LocalDate datum = datum_datePicker.getValue();
        String mandatRaw = duzinaMandata_txtField.getText().trim();

        if (adresa.isEmpty() || telefon.isEmpty() || jmb.isEmpty() || ime.isEmpty() || prezime.isEmpty() || datum == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Sva obavezna polja moraju biti popunjena.");
            return;
        }

        int mandat = 0;
        try {
            mandat = Integer.parseInt(mandatRaw);
        } catch (NumberFormatException e) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Trajanje mandata mora biti broj.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlProvjera = "select JMB from direktor where JMB = ?";
            boolean postojiDir = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlProvjera)) {
                ps.setString(1, jmb);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) postojiDir = true;
                }
            }

            if (!postojiDir) {
                String insertDir = "insert into direktor (JMB, Ime, Prezime, Telefon, DatumStupanjaNaDuznost, TrajanjeMandata) values (?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insertDir)) {
                    ps.setString(1, jmb);
                    ps.setString(2, ime);
                    ps.setString(3, prezime);
                    ps.setString(4, dirTel);
                    ps.setDate(5, Date.valueOf(datum));
                    ps.setInt(6, mandat);
                    ps.executeUpdate();
                }
            } else {
                String updateDir = "update direktor set Ime=?, Prezime=?, Telefon=?, DatumStupanjaNaDuznost=?, TrajanjeMandata=? where JMB=?";
                try (PreparedStatement ps = conn.prepareStatement(updateDir)) {
                    ps.setString(1, ime);
                    ps.setString(2, prezime);
                    ps.setString(3, dirTel);
                    ps.setDate(4, Date.valueOf(datum));
                    ps.setInt(5, mandat);
                    ps.setString(6, jmb);
                    ps.executeUpdate();
                }
            }

            String updateZadruga = "update zadruga set Adresa=?, Telefon=?, Aktivna=?, Direktor_JMB=? where JIB=?";
            try (PreparedStatement ps = conn.prepareStatement(updateZadruga)) {
                ps.setString(1, adresa);
                ps.setString(2, telefon);
                ps.setByte(3, (byte) (aktivna ? 1 : 0));
                ps.setString(4, jmb);
                ps.setString(5, selektovaniJib);
                ps.executeUpdate();
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Izmjene su uspješno sačuvane.");
            zatvoriProzor();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Sistemska greška tokom čuvanja podataka.");
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML
    private void ponistiIzmjene() {
        zatvoriProzor();
    }

    private void zatvoriProzor() {
        Stage stage = (Stage) naziv_txtField.getScene().getWindow();
        stage.close();
    }

    private String parsirajJmb(String tekst) {
        return tekst.substring(tekst.indexOf("(") + 1, tekst.indexOf(")"));
    }

    private void prikaziPoruku(Alert.AlertType tip, String naslov, String sadrzaj) {
        Alert alert = new Alert(tip);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }
}
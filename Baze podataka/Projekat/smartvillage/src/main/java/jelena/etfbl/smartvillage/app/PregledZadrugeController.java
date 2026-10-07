package jelena.etfbl.smartvillage.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PregledZadrugeController {

    @FXML private TextField naziv_txtField;
    @FXML private TextField jib_txtField;
    @FXML private TextField adresa_txtField;
    @FXML private TextField telefon_txtField;
    @FXML private CheckBox aktivna_checkBox;

    @FXML private TextField jmb_txtField;
    @FXML private TextField ime_txtField;
    @FXML private TextField prezime_txtField;
    @FXML private TextField dirTelefon_txtField;
    @FXML private DatePicker datum_datePicker;
    @FXML private TextField duzinaMandata_txtField;

    private String trenutniJib;

    public void postaviZadrugu(String jib) {
        this.trenutniJib = jib;
        osvjeziPodatke();
    }

    private void osvjeziPodatke() {
        String query = "select * from vPregledZadruge where JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, trenutniJib);
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
            prikaziPoruku("Greška", "Nije moguće učitati podatke o zadruzi preko pogleda baze.");
        }
    }

    @FXML
    private void otvoriUredjivanje() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/urediZadrugu.fxml"));
            Scene scene = new Scene(loader.load());

            UrediZadruguController controller = loader.getController();
            controller.inicijalizujPodatke(trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Uređivanje zadruge - JIB: " + trenutniJib);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());

            stage.showAndWait();

            osvjeziPodatke();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za uređivanje.");
        }
    }

    @FXML
    private void otvoriPregledZaposlenih() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledZaposlenih.fxml"));
            Scene scene = new Scene(loader.load());

            PregledZaposlenihController controller = loader.getController();
            controller.postaviZadrugu(trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Pregled zaposlenih");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(jmb_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled zaposlenih.");
        }
    }

    @FXML
    private void otvoriPregledHabova() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledHabova.fxml"));
            Scene scene = new Scene(loader.load());

            PregledHabovaController controller = loader.getController();
            controller.postaviZadrugu(trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Pregled habova zadruge");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled habova.");
        }
    }

    @FXML
    private void otvoriPregledParcela() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledParcela.fxml"));
            Scene scene = new Scene(loader.load());

            PregledParcelaController controller = loader.getController();
            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Pregled parcela i zasada zadruge");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled parcela.");
        }
    }

    @FXML
    private void otvoriProzorPregledZasada() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/zasadiPregled.fxml"));
            Parent root = loader.load();

            ZasadiPregledController controller = loader.getController();
            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Pregled svih zasada");
            stage.setScene(new Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void otvoriPregledKultura() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledKultura.fxml"));
            Scene scene = new Scene(loader.load());

            PregledKulturaController controller = loader.getController();

            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Šifrarnik i pregled kultura zadruge");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled kultura.");
        }
    }

    @FXML
    private void otvoriPregledPlastenika() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledPlastenika.fxml"));
            Scene scene = new Scene(loader.load());

            PregledPlastenikaController controller = loader.getController();
            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Upravljanje plastenicima zadruge");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled plastenika.");
        }
    }

    @FXML
    private void otvoriPregledPrinosa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledPrinosa.fxml"));
            Scene scene = new Scene(loader.load());

            PregledPrinosaController controller = loader.getController();
            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Evidencija ostvarenih prinosa");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za pregled prinosa.");
        }
    }

    @FXML
    private void otvoriPregledUredjaja() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledUredjaja.fxml"));
            Scene scene = new Scene(loader.load());

            PregledUredjajaController controller = loader.getController();
            controller.postaviZadrugu(this.trenutniJib);

            Stage stage = new Stage();
            stage.setTitle("Upravljanje IoT infrastrukturom");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(naziv_txtField.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            prikaziPoruku("Greška", "Nije moguće otvoriti prozor za uređaje.");
        }
    }

    private void prikaziPoruku(String naslov, String sadrzaj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(naslov);
        alert.setHeaderText(null);
        alert.setContentText(sadrzaj);
        alert.showAndWait();
    }
}
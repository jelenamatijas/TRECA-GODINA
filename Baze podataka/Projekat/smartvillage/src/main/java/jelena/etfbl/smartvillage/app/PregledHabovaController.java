package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;
import jelena.etfbl.smartvillage.pomocneKlase.Hub;

import java.sql.*;

public class PregledHabovaController {

    @FXML private TableView<Hub> hub_tableView;
    @FXML private TableColumn<Hub, String> nazivHub_col;
    @FXML private TableColumn<Hub, String> opisHub_col;

    @FXML private ListView<String> zaposleni_listView;
    @FXML private ComboBox<String> slobodniRadnici_cmbBox;

    @FXML private TextField noviNaziv_txtField;
    @FXML private TextArea noviOpis_txtArea;
    @FXML private TextField filter;

    private String trenutniJibZadruge;
    private ObservableList<Hub> listaHabova = FXCollections.observableArrayList();
    private ObservableList<String> radniciUHubu = FXCollections.observableArrayList();
    private ObservableList<String> ostaliRadniciZadruge = FXCollections.observableArrayList();

    public void postaviZadrugu(String jib) {
        this.trenutniJibZadruge = jib;
        ucitajHabove();
        ucitajSveRadnikeZadrugeZaCmbBox(null);
    }

    @FXML
    private void initialize() {
        nazivHub_col.setCellValueFactory(cellData -> cellData.getValue().nazivProperty());
        opisHub_col.setCellValueFactory(cellData -> cellData.getValue().opisProperty());

        hub_tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ucitajZaposleneZaHab(newValue.getNaziv());
                ucitajSveRadnikeZadrugeZaCmbBox(newValue.getNaziv());
            } else {
                zaposleni_listView.getItems().clear();
                slobodniRadnici_cmbBox.getItems().clear();
            }
        });


    }

    private void ucitajHabove() {
        listaHabova.clear();
        String query = "select * from hub where ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaHabova.add(new Hub(
                            rs.getString("Naziv"),
                            rs.getString("Opis")
                    ));
                }
            }
            hub_tableView.setItems(listaHabova);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajZaposleneZaHab(String nazivHaba) {
        radniciUHubu.clear();

        String query = "select * from vPregledRadnikaUHubu where HUB_Naziv = ? and HUB_ZADRUGA_JIB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nazivHaba);
            ps.setString(2, trenutniJibZadruge);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    radniciUHubu.add(rs.getString("Ime") + " " + rs.getString("Prezime") + " (" + rs.getString("JMB") + ")");
                }
            }
            zaposleni_listView.setItems(radniciUHubu);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ucitajSveRadnikeZadrugeZaCmbBox(String trenutniHub) {
        ostaliRadniciZadruge.clear();
        String query = "select JMB, Ime, Prezime from zaposleni where ZADRUGA_JIB = ? ";
        if (trenutniHub != null) {
            query += "and JMB not in (select ZAPOSLENI_JMB from HUB_has_ZAPOSLENI where HUB_Naziv = ? and HUB_ZADRUGA_JIB = ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
            if (trenutniHub != null) {
                ps.setString(2, trenutniHub);
                ps.setString(3, trenutniJibZadruge);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ostaliRadniciZadruge.add(rs.getString("Ime") + " " + rs.getString("Prezime") + " (" + rs.getString("JMB") + ")");
                }
            }
            slobodniRadnici_cmbBox.setItems(ostaliRadniciZadruge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void kreirajNoviHab() {
        String naziv = noviNaziv_txtField.getText().trim();
        String opis = noviOpis_txtArea.getText().trim();

        if (naziv.isEmpty() || opis.isEmpty()) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate unijeti naziv i opis za novi hab.");
            return;
        }

        String query = "insert into HUB (Naziv, Opis, ZADRUGA_JIB) values (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, naziv);
            ps.setString(2, opis);
            ps.setString(3, trenutniJibZadruge);
            ps.executeUpdate();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Novi hab je uspješno kreiran.");
            noviNaziv_txtField.clear();
            noviOpis_txtArea.clear();
            ucitajHabove();
        } catch (SQLException e) {
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće kreirati hab (vjerovatno naziv već postoji u ovoj zadruzi).");
        }
    }

    @FXML
    private void obrisiHab() {
        Hub selektovaniHub = hub_tableView.getSelectionModel().getSelectedItem();
        if (selektovaniHub == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Morate selektovati hab iz tabele za brisanje.");
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteVeza = "delete from HUB_has_ZAPOSLENI where HUB_Naziv = ? and HUB_ZADRUGA_JIB = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteVeza)) {
                ps.setString(1, selektovaniHub.getNaziv());
                ps.setString(2, trenutniJibZadruge);
                ps.executeUpdate();
            }

            String deleteHub = "delete from hub where Naziv = ? and ZADRUGA_JIB = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteHub)) {
                ps.setString(1, selektovaniHub.getNaziv());
                ps.setString(2, trenutniJibZadruge);
                ps.executeUpdate();
            }

            conn.commit();
            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Hab je uspješno obrisan.");
            hub_tableView.getSelectionModel().clearSelection();
            ucitajHabove();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            prikaziPoruku(Alert.AlertType.ERROR, "Greška", "Nije moguće obrisati hab jer su za njega vezani IoT uređaji ili plastenici.");
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @FXML
    private void dodajZaposlenogUHab() {
        Hub selektovaniHub = hub_tableView.getSelectionModel().getSelectedItem();
        String odabraniRadnik = slobodniRadnici_cmbBox.getValue();

        if (selektovaniHub == null || odabraniRadnik == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Selektujte hab iz tabele i radnika iz padajućeg menija.");
            return;
        }

        String jmb = parsirajJmb(odabraniRadnik);
        String query = "insert into HUB_has_ZAPOSLENI (HUB_Naziv, HUB_ZADRUGA_JIB, ZAPOSLENI_JMB) values (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, selektovaniHub.getNaziv());
            ps.setString(2, trenutniJibZadruge);
            ps.setString(3, jmb);
            ps.executeUpdate();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Zaposleni je raspoređen u hab.");
            ucitajZaposleneZaHab(selektovaniHub.getNaziv());
            ucitajSveRadnikeZadrugeZaCmbBox(selektovaniHub.getNaziv());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ukloniZaposlenogIzHaba() {
        Hub selektovaniHub = hub_tableView.getSelectionModel().getSelectedItem();
        String selektovaniRadnikTekst = zaposleni_listView.getSelectionModel().getSelectedItem();

        if (selektovaniHub == null || selektovaniRadnikTekst == null) {
            prikaziPoruku(Alert.AlertType.WARNING, "Upozorenje", "Selektujte hab u tabeli i radnika iz liste za uklanjanje.");
            return;
        }

        String jmb = parsirajJmb(selektovaniRadnikTekst);
        String query = "delete from HUB_has_ZAPOSLENI where HUB_Naziv = ? and HUB_ZADRUGA_JIB = ? and ZAPOSLENI_JMB = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, selektovaniHub.getNaziv());
            ps.setString(2, trenutniJibZadruge);
            ps.setString(3, jmb);
            ps.executeUpdate();

            prikaziPoruku(Alert.AlertType.INFORMATION, "Uspjeh", "Zaposleni je uklonjen iz ovog haba.");
            ucitajZaposleneZaHab(selektovaniHub.getNaziv());
            ucitajSveRadnikeZadrugeZaCmbBox(selektovaniHub.getNaziv());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void zatvoriProzor() {
        Stage stage = (Stage) zaposleni_listView.getScene().getWindow();
        stage.close();
    }

    @FXML void filterTyped(){
        String filterText = filter.getText().trim();
        String string = filterText + "%";
        System.out.println(string);

        listaHabova.clear();
        String query = "select * from hub where ZADRUGA_JIB = ? and Naziv like ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, trenutniJibZadruge);
            ps.setString(2, string);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaHabova.add(new Hub(
                            rs.getString("Naziv"),
                            rs.getString("Opis")
                    ));
                }
            }
            hub_tableView.setItems(listaHabova);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
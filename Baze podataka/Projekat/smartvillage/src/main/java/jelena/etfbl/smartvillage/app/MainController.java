package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainController {
    @FXML private ChoiceBox<String> pregledZadruga_choiceBox;
    @FXML private ListView<String> pregledZadruga_listView;
    @FXML private ListView<String> pregledZadrugara_listView;
    @FXML private ChoiceBox<String> pregledZadrugara_choiceBox;

    @FXML public void initialize(){
        ObservableList<String> opcije = FXCollections.observableArrayList(
                "Prikaz aktivnih zadruga",
                "Prikaz neaktivnih zadruga",
                "Prikaz svih zadruga"
        );
        pregledZadruga_choiceBox.setItems(opcije);
        pregledZadruga_choiceBox.setValue("Prikaz aktivnih zadruga");
        ucitajZadruge("Prikaz aktivnih zadruga");

        ObservableList<String> opcijeZadrugara = FXCollections.observableArrayList(
                "Prikaz aktivnih zadrugara",
                "Prikaz neaktivnih zadrugara",
                "Prikaz svih zadrugara"
        );
        pregledZadrugara_choiceBox.setItems(opcijeZadrugara);
        pregledZadrugara_choiceBox.setValue("Prikaz aktivnih zadrugara");
        ucitajZadrugare("Prikaz aktivnih zadrugara");

        pregledZadruga_choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, old, newValue) -> {
            if(newValue != null){
                ucitajZadruge(newValue);
            }
        });

        pregledZadrugara_choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, old, newValue) -> {
            if(newValue != null){
                ucitajZadrugare(newValue);
            }
        });

        pregledZadrugara_listView.setOnMouseClicked(event -> {
            String selektovaniZadrugar = pregledZadrugara_listView.getSelectionModel().getSelectedItem();

            if (selektovaniZadrugar != null && !selektovaniZadrugar.startsWith("GREŠKA")) {
                otvoriProzorZaIzmjenuZadrugara(selektovaniZadrugar);
            }
        });

        // Detekcija klika na zadrugu u ListView-u
        pregledZadruga_listView.setOnMouseClicked(event -> {
            String selektovanaZadruga = pregledZadruga_listView.getSelectionModel().getSelectedItem();

            if (selektovanaZadruga != null && !selektovanaZadruga.startsWith("GREŠKA")) {
                int pocetakJIB = selektovanaZadruga.indexOf("(JIB:") + 5;
                int krajJIB = selektovanaZadruga.lastIndexOf(")");
                if (pocetakJIB >= 5 && krajJIB != -1) {
                    String jib = selektovanaZadruga.substring(pocetakJIB, krajJIB).trim();
                    otvoriProzorZaPregledZadruge(jib);
                }
            }
        });
    }

    @FXML private void otvoriProzorZaDodavanjeZadruge(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/kreirajZadrugu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage noviProzor = new Stage();
            noviProzor.setTitle("Smart Village - Kreiranje nove zadruge");
            noviProzor.setScene(scene);

            // Osvježavamo listu zadruga nakon zatvaranja prozora za kreiranje
            noviProzor.showAndWait();
            ucitajZadruge(pregledZadruga_choiceBox.getValue());

        }catch (IOException e){
            e.printStackTrace();
            System.out.println("GRESKA: Nije moguce ucitati prozor za kreiranje nove zadruge.");
        }
    }

    @FXML
    private void otvoriProzorZaDodavanjeZadrugara() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/noviZadrugar.fxml"));
            Scene scene = new Scene(loader.load());

            NoviZadrugarController controller = loader.getController();
            controller.PostaviPostojeceZadrugare(null);

            Stage stage = new Stage();
            stage.setTitle("Smart Village - Kreiranje novog zadrugara");
            stage.setScene(scene);

            stage.showAndWait();

            KreirajZadruguController.ZadrugarModel novi = controller.getKreiraniZadrugar();

            if (novi != null) {
                snimiNovogZadrugaraUBazu(novi);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("GRESKA: Nije moguće učitati prozor za dodavanje zadrugara.");
        }
    }

    private void ucitajZadruge(String filter){
        ObservableList<String> zadruge = FXCollections.observableArrayList();
        String query = "select Naziv, JIB from zadruga";

        if(filter.equals("Prikaz aktivnih zadruga")){
            query += " where Aktivna = 1";
        }else if(filter.equals("Prikaz neaktivnih zadruga")){
            query += " where Aktivna = 0";
        }

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet result = ps.executeQuery()){

            while(result.next()){
                zadruge.add(result.getString("Naziv") + " (JIB:" + result.getString("JIB") + ")");
            }
        }catch(SQLException e){
            zadruge.add("GREŠKA pri učitavanju podataka!");
            e.printStackTrace();
        }

        pregledZadruga_listView.setItems(zadruge);
    }

    private void ucitajZadrugare(String filter){
        ObservableList<String> zadrugari = FXCollections.observableArrayList();
        String query = "select Ime, Prezime, JMB from zadrugar";

        if(filter.equals("Prikaz aktivnih zadrugara")){
            query += " where Status = 1";
        }else if(filter.equals("Prikaz neaktivnih zadrugara")){
            query += " where Status = 0";
        }

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet result = ps.executeQuery()){

            while(result.next()){
                zadrugari.add(result.getString("Ime") + " "
                        + result.getString("Prezime") + " (JMB: "
                        + result.getString("JMB") + ")");
            }
        }catch(SQLException e){
            zadrugari.add("GREŠKA pri učitavanju podataka!");
            e.printStackTrace();
        }

        pregledZadrugara_listView.setItems(zadrugari);
    }

    private void otvoriProzorZaIzmjenuZadrugara(String selektovani) {
        int pocetakJMB = selektovani.indexOf("(JMB: ") + 6;
        int krajJMB = selektovani.lastIndexOf(")");
        if (pocetakJMB < 6 || krajJMB == -1) return;

        String jmb = selektovani.substring(pocetakJMB, krajJMB);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/izmjeniZadrugara.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            IzmjeniZadrugaraController controller = fxmlLoader.getController();
            controller.InicijalizujPodatke(jmb);

            controller.setRefreshCallback(() -> {
                ucitajZadrugare(pregledZadrugara_choiceBox.getValue());
                ucitajZadruge(pregledZadruga_choiceBox.getValue());
            });

            Stage noviProzor = new Stage();
            noviProzor.setTitle("Izmjena podataka o zadrugaru");
            noviProzor.setScene(scene);
            noviProzor.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("GRESKA: Nije moguće učitati prozor za izmjenu zadrugara.");
        }
    }


    private void snimiNovogZadrugaraUBazu(KreirajZadruguController.ZadrugarModel zadrugar) {
        String sqlProvjeraJMB = "select JMB from zadrugar where JMB = ?";
        String sqlInsertZadrugar = "insert into zadrugar (JMB, Ime, Prezime, Telefon, Status) values (?, ?, ?, ?, ?)";
        String sqlInsertParcela = "insert into parcela (ID_Parcele, Naziv, Povrsina, KatastarskaOpstina, ZADRUGAR_JMB) values (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psProvjera = conn.prepareStatement(sqlProvjeraJMB)) {
                psProvjera.setString(1, zadrugar.getJmb());
                try (ResultSet rs = psProvjera.executeQuery()) {
                    if (rs.next()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Zadrugar sa JMB-om " + zadrugar.getJmb() + " već postoji u bazi!");
                        alert.showAndWait();
                        conn.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psZadrugar = conn.prepareStatement(sqlInsertZadrugar)) {
                psZadrugar.setString(1, zadrugar.getJmb());
                psZadrugar.setString(2, zadrugar.getIme());
                psZadrugar.setString(3, zadrugar.getPrezime());
                psZadrugar.setString(4, zadrugar.getTelefon());
                psZadrugar.setInt(5, zadrugar.getStatus());
                psZadrugar.executeUpdate();
            }

            if (zadrugar.getParcele() != null && !zadrugar.getParcele().isEmpty()) {
                try (PreparedStatement psParcela = conn.prepareStatement(sqlInsertParcela)) {
                    for (IzmjeniZadrugaraController.ParcelaData p : zadrugar.getParcele()) {
                        psParcela.setInt(1, p.id);
                        psParcela.setString(2, p.naziv);
                        psParcela.setDouble(3, p.povrsina);
                        psParcela.setInt(4, p.katastarskaOpstina);
                        psParcela.setString(5, zadrugar.getJmb());
                        psParcela.executeUpdate();
                    }
                }
            }

            conn.commit();

            ucitajZadrugare(pregledZadrugara_choiceBox.getValue());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Zadrugar i njegove parcele su uspješno sačuvani u bazu!");
            alert.showAndWait();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Sistemska greška pri upisu zadrugara u bazu podataka.");
            alert.showAndWait();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void otvoriProzorZaPregledZadruge(String jib) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/pregledZadruge.fxml"));
            Scene scene = new Scene(loader.load());

            PregledZadrugeController controller = loader.getController();
            controller.postaviZadrugu(jib);

            Stage stage = new Stage();
            stage.setTitle("Pregled zadruge - JIB: " + jib);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            ucitajZadruge(pregledZadruga_choiceBox.getValue());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("GRESKA: Nije moguće učitati prozor za pregled zadruge.");
        }
    }
}
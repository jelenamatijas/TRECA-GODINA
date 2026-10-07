package jelena.etfbl.smartvillage.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jelena.etfbl.smartvillage.app.KreirajZadruguController.ZadrugarModel;
import jelena.etfbl.smartvillage.app.IzmjeniZadrugaraController.ParcelaData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoviZadrugarController {

    @FXML private TextField jmb_txt;
    @FXML private TextField ime_txt;
    @FXML private TextField prezime_txt;
    @FXML private TextField telefon_txt;
    @FXML private ComboBox<String> status_cmb;
    @FXML private ListView<String> parcele_listView;

    private List<ParcelaData> privremeneParcele = new ArrayList<>();
    private ObservableList<ZadrugarModel> trenutniZadrugariNaEkranu;
    private ZadrugarModel kreiraniZadrugar = null;

    @FXML
    private void initialize() {
        status_cmb.setItems(FXCollections.observableArrayList("Aktivan (1)", "Neaktivan (0)"));
        status_cmb.getSelectionModel().selectFirst();
    }

    public void PostaviPostojeceZadrugare(ObservableList<ZadrugarModel> lista) {
        this.trenutniZadrugariNaEkranu = lista;
    }

    public ZadrugarModel getKreiraniZadrugar() {
        return kreiraniZadrugar;
    }

    @FXML
    private void dodajNovuParcelu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/jelena/etfbl/smartvillage/novaParcela.fxml"));
            Parent root = loader.load();

            NovaParcelaController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Dodaj parcelu za novog zadrugara");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(jmb_txt.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            ParcelaData nova = controller.getKreiranaParcela();
            if (nova != null) {
                if (privremeneParcele.stream().anyMatch(p -> p.id == nova.id)) {
                    prikaziUpozorenje("Parcela sa tim ID-om je već dodata u listu.");
                    return;
                }
                privremeneParcele.add(nova);
                parcele_listView.getItems().add("ID: " + nova.id + " - " + nova.naziv + " (" + nova.povrsina + " ha) [K.O. " + nova.katastarskaOpstina + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ukloniParcelu() {
        int index = parcele_listView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            privremeneParcele.remove(index);
            parcele_listView.getItems().remove(index);
        } else {
            prikaziUpozorenje("Selektujte parcelu iz liste za uklanjanje.");
        }
    }

    @FXML
    private void potvrdi() {
        String jmb = jmb_txt.getText().trim();
        String ime = ime_txt.getText().trim();
        String prezime = prezime_txt.getText().trim();
        String telefon = telefon_txt.getText().trim();

        if (jmb.isEmpty() || ime.isEmpty()) {
            prikaziUpozorenje("JMB i Ime su obavezna polja!");
            return;
        }

        if (trenutniZadrugariNaEkranu != null && trenutniZadrugariNaEkranu.stream().anyMatch(z -> z.getJmb().equals(jmb))) {
            prikaziUpozorenje("Zadrugar sa ovim JMB je već dodat u listu zadruge!");
            return;
        }

        int status = status_cmb.getSelectionModel().getSelectedIndex() == 0 ? 1 : 0;

        kreiraniZadrugar = new ZadrugarModel(jmb, ime, prezime, telefon, status, true);
        kreiraniZadrugar.setParcele(privremeneParcele);

        ((Stage) jmb_txt.getScene().getWindow()).close();
    }

    @FXML
    private void odustani() {
        kreiraniZadrugar = null;
        ((Stage) jmb_txt.getScene().getWindow()).close();
    }

    private void prikaziUpozorenje(String poruka) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Upozorenje");
        alert.setHeaderText(null);
        alert.setContentText(poruka);
        alert.showAndWait();
    }
}
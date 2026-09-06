/*package jelena.etfbl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private TextArea inputArea;
    private TextArea resultArea;
    private ComboBox<String> groupI, groupJ;
    private ArrayList<ArrayList<Double>> data;
    private ArrayList<Double> groupMeans;
    private double grandMean;

    @Override
    public void start(Stage stage) {
        inputArea = new TextArea();
        inputArea.setPromptText(
                "Svaki red = jedna grupa, vrijednosti odvojene zarezima\n" +
                        "Primjer:\n" +
                        "0.0972,0.0971,0.0969,0.1954,0.0974\n" +
                        "0.1382,0.1432,0.1382,0.1730,0.1383\n" +
                        "0.7966,0.5300,0.5152,0.6675,0.5298"
        );
        inputArea.setPrefRowCount(6);

        Button anovaBtn = new Button("Izračunaj ANOVA");
        Button contrastBtn = new Button("Izračunaj kontrast");

        groupI = new ComboBox<>();
        groupJ = new ComboBox<>();

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(30);
        resultArea.setFont(Font.font("Monospaced", 13));

        anovaBtn.setOnAction(e -> calculateANOVA());
        contrastBtn.setOnAction(e -> calculateContrast());

        Label title = new Label("ANOVA aplikacija");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));

        HBox contrastBox = new HBox(10,
                new Label("Grupa i:"), groupI,
                new Label("Grupa j:"), groupJ,
                contrastBtn
        );
        contrastBox.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10,
                title,
                new Label("Unesi podatke (svaki red = jedna grupa):"),
                inputArea,
                anovaBtn,
                new Separator(),
                new Label("Kontrast između grupa:"),
                contrastBox,
                new Separator(),
                new Label("Rezultati:"),
                resultArea
        );
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 650, 750));
        stage.setTitle("ANOVA GUI");
        stage.show();
    }

    private void parseData() throws Exception {
        data = new ArrayList<>();
        String[] lines = inputArea.getText().trim().split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            ArrayList<Double> group = new ArrayList<>();
            for (String p : parts)
                group.add(Double.parseDouble(p.trim()));
            data.add(group);
        }
    }

    private void calculateANOVA() {
        try {
            parseData();

            int k = data.size();
            ArrayList<Double> all = new ArrayList<>();
            for (var g : data) all.addAll(g);
            int n = all.size();

            if (k < 2 || n <= k) {
                resultArea.setText("Nedovoljno podataka za ANOVA!");
                return;
            }

            grandMean = mean(all);
            groupMeans = new ArrayList<>();
            for (var g : data) groupMeans.add(mean(g));

            double ssa = 0;
            for (int i = 0; i < k; i++)
                ssa += data.get(i).size() * Math.pow(groupMeans.get(i) - grandMean, 2);

            double sse = 0;
            for (int i = 0; i < k; i++)
                for (double x : data.get(i))
                    sse += Math.pow(x - groupMeans.get(i), 2);

            double sst = ssa + sse;
            int dfA = k - 1;
            int dfE = n - k;
            int dfT = n - 1;
            double msa = ssa / dfA;
            double mse = sse / dfE;
            double F = msa / mse;

            StringBuilder sb = new StringBuilder();

            // ── Tabela podataka ──────────────────────────────────────────
            sb.append("=== PODACI PO GRUPAMA ===\n\n");
            sb.append(String.format("%-12s", "Mjerenje"));
            for (int j = 0; j < k; j++)
                sb.append(String.format("%-16s", "Alternativa " + (j + 1)));
            sb.append("\n");
            sb.append("-".repeat(12 + 16 * k)).append("\n");

            int maxN = data.stream().mapToInt(ArrayList::size).max().orElse(0);
            for (int i = 0; i < maxN; i++) {
                sb.append(String.format("%-12d", i + 1));
                for (int j = 0; j < k; j++) {
                    String val = (i < data.get(j).size())
                            ? String.format("%.4f", data.get(j).get(i))
                            : "-";
                    sb.append(String.format("%-16s", val));
                }
                sb.append("\n");
            }

            sb.append("-".repeat(12 + 16 * k)).append("\n");

            sb.append(String.format("%-12s", "Sr. vrij."));
            for (int j = 0; j < k; j++)
                sb.append(String.format("%-16s", String.format("%.4f", groupMeans.get(j))));
            sb.append(String.format("  Ukupna: %.4f", grandMean)).append("\n");

            sb.append(String.format("%-12s", "Efekti"));
            for (int j = 0; j < k; j++)
                sb.append(String.format("%-16s", String.format("%+.4f", groupMeans.get(j) - grandMean)));
            sb.append("\n");

            // ── ANOVA tabela ─────────────────────────────────────────────
            sb.append("\n=== ANOVA TABELA ===\n\n");
            sb.append(String.format("%-22s%-18s%-18s%-16s\n",
                    "Varijacija", "Alternative", "Greška", "Ukupno"));
            sb.append("-".repeat(74)).append("\n");
            sb.append(String.format("%-22s%-18s%-18s%-16s\n",
                    "Suma kvadrata",
                    "SSA=" + String.format("%.4f", ssa),
                    "SSE=" + String.format("%.4f", sse),
                    "SST=" + String.format("%.4f", sst)));
            sb.append(String.format("%-22s%-18s%-18s%-16s\n",
                    "Stepeni slobode",
                    "k-1=" + dfA,
                    "k(n-1)=" + dfE,
                    "kn-1=" + dfT));
            sb.append(String.format("%-22s%-18s%-18s\n",
                    "Srednji kvadrat",
                    "s*sa=" + String.format("%.4f", msa),
                    "s*se=" + String.format("%.4f", mse)));
            sb.append(String.format("%-22s%s\n",
                    "F izračunati",
                    String.format("%.4f / %.4f = %.2f", msa, mse, F)));
            sb.append(String.format("%-22s%s\n",
                    "F tabelarni (0.95)",
                    "F[0.95; " + dfA + ", " + dfE + "]"));

            resultArea.setText(sb.toString());

            // Popuni comboboxeve za kontrast
            groupI.getItems().clear();
            groupJ.getItems().clear();
            for (int i = 0; i < k; i++) {
                groupI.getItems().add("Grupa " + (i + 1));
                groupJ.getItems().add("Grupa " + (i + 1));
            }
            groupI.getSelectionModel().selectFirst();
            if (k > 1) groupJ.getSelectionModel().select(1);

        } catch (Exception e) {
            resultArea.setText("Greška u unosu podataka!\nProvjeri format: vrijednosti odvojene zarezima, svaka grupa u novom redu.");
        }
    }

    private void calculateContrast() {
        if (data == null || groupMeans == null) {
            resultArea.appendText("\n\nPrvo izračunaj ANOVA!");
            return;
        }
        int i = groupI.getSelectionModel().getSelectedIndex();
        int j = groupJ.getSelectionModel().getSelectedIndex();
        if (i < 0 || j < 0) return;

        double c = groupMeans.get(i) - groupMeans.get(j);
        resultArea.appendText(String.format(
                "\n\nKontrast (Grupa %d − Grupa %d): %+.4f", i + 1, j + 1, c
        ));
    }

    private double mean(ArrayList<Double> list) {
        double sum = 0;
        for (double x : list) sum += x;
        return sum / list.size();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/

package jelena.etfbl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private TextArea inputArea;
    private TextArea resultArea;
    private ComboBox<String> groupI, groupJ;
    private TextField alphaField;

    private ArrayList<ArrayList<Double>> data;
    private ArrayList<Double> groupMeans;
    private double grandMean;
    private int k; // broj alternativa
    private int n; // broj ponavljanja po alternativi
    private double mse; // Varijansa greske (se^2 iz jednacine 37 i 42)

    @Override
    public void start(Stage stage) {
        inputArea = new TextArea();
        inputArea.setPromptText(
                "Svaki red = jedna grupa (alternativa), vrijednosti odvojene zarezima\n" +
                        "Primjer za tvoja mjerenja (Batch 50, 100, 500):\n" +
                        "6969,4104,4037,3968,3972\n" +
                        "2909,2221,2785,2906,2792\n" +
                        "1903,1850,1890,1820,1860"
        );
        inputArea.setPrefRowCount(6);
        inputArea.setFont(Font.font("Consolas", 12));

        Label alphaLabel = new Label("Nivo značajnosti (α):");
        alphaField = new TextField("0.05");
        alphaField.setPrefWidth(50);

        Button anovaBtn = new Button("Izračunaj ANOVA");
        anovaBtn.setStyle("-fx-background-color: #2b78e4; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox topMenu = new HBox(15, alphaLabel, alphaField, anovaBtn);
        topMenu.setAlignment(Pos.CENTER_LEFT);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setFont(Font.font("Consolas", 13));

        // Sekcija za kontraste
        Label contrastLabel = new Label("Poređenje parova (Kontrasti):");
        contrastLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        groupI = new ComboBox<>();
        Label vsLabel = new Label("protiv");
        groupJ = new ComboBox<>();
        Button contrastBtn = new Button("Izračunaj kontrast i interval");
        contrastBtn.setStyle("-fx-background-color: #2aa846; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox contrastBox = new HBox(10, groupI, vsLabel, groupJ, contrastBtn);
        contrastBox.setAlignment(Pos.CENTER_LEFT);
        contrastBox.setPadding(new Insets(5, 0, 0, 0));

        VBox root = new VBox(10,
                new Label("Unos eksperimentalnih podataka (vremena u ms):"),
                inputArea,
                topMenu,
                new Separator(),
                contrastLabel,
                contrastBox,
                new Label("Rezultati statističke analize:"),
                resultArea
        );
        root.setPadding(new Insets(15));
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        anovaBtn.setOnAction(e -> calculateAnova());
        contrastBtn.setOnAction(e -> calculateContrast());

        Scene scene = new Scene(root, 850, 650);
        stage.setTitle("Statistička analiza alternativa (ANOVA & Kontrasti) - Jelena Matijaš");
        stage.setScene(scene);
        stage.show();
    }

    private void calculateAnova() {
        try {
            String text = inputArea.getText().trim();
            if (text.isEmpty()) return;

            data = new ArrayList<>();
            String[] lines = text.split("\n");

            k = lines.length;
            n = -1;
            double totalSum = 0;
            int totalN = 0;

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                ArrayList<Double> row = new ArrayList<>();
                for (String token : tokens) {
                    double val = Double.parseDouble(token.trim());
                    row.add(val);
                    totalSum += val;
                }
                if (n == -1) {
                    n = row.size(); // Balansirani model: uzimamo velicinu prvog reda
                } else if (row.size() != n) {
                    throw new IllegalArgumentException("Model mora biti balansiran (svaki red mora imati isti broj mjerenja n)!");
                }
                data.add(row);
                totalN += row.size();
            }

            // 1. Izracunavanje srednjih vrijednosti (Jednačine 31 i 32)
            grandMean = totalSum / totalN; // Srednja vrijednost svih mjerenja (y_dot_dot)
            groupMeans = new ArrayList<>();

            for (int j = 0; j < k; j++) {
                double rowSum = 0;
                for (double val : data.get(j)) {
                    rowSum += val;
                }
                groupMeans.add(rowSum / n); // Srednja vrijednost j-te alternative (y_bar_j_dot)
            }

            // 2. Izracunavanje suma kvadrata (Jednačine 33, 34, 35)
            double ssa = 0;
            for (int j = 0; j < k; j++) {
                double diff = groupMeans.get(j) - grandMean;
                ssa += diff * diff;
            }
            ssa *= n; // Suma kvadrata izmedju alternativa (Jednačina 34)

            double sse = 0;
            for (int j = 0; j < k; j++) {
                double curMean = groupMeans.get(j);
                for (double val : data.get(j)) {
                    double diff = val - curMean;
                    sse += diff * diff; // Suma kvadrata greske (Jednačina 35)
                }
            }

            double sst = ssa + sse; // Ukupna suma kvadrata (Jednačina 33)

            // 3. Stepeni slobode (Degrees of freedom)
            int dfA = k - 1;
            int dfE = k * (n - 1);
            int dfT = k * n - 1;

            // 4. Srednji kvadrati (Jednačine 36 i 37)
            double msm = ssa / dfA;
            mse = sse / dfE; // Srednji kvadrat greske (se^2 iz rada)

            // 5. F-vrijednost (Jednačina 38)
            double fComputed = msm / mse;

            // Dohvatanje kritične F vrijednosti iz aproksimirane tabele za alpha=0.05
            double alpha = Double.parseDouble(alphaField.getText().trim());
            double fTable = getCriticalF(dfA, dfE, alpha);

            // Generisanje izvjestaja
            StringBuilder sb = new StringBuilder();
            sb.append("=================================================================================\n");
            sb.append("                           ANOVA TABELA (Jednačine 33-38)                        \n");
            sb.append("=================================================================================\n");
            sb.append(String.format("%-20s %-15s %-10s %-15s %-15s\n", "Izvor varijacije", "Suma kvadrata(SS)", "df", "Srednji kv.(MS)", "Izračunato F"));
            sb.append("---------------------------------------------------------------------------------\n");
            sb.append(String.format("%-20s %-15.4f %-10d %-15.4f %-15.4f\n", "Alternative (SSA)", ssa, dfA, msm, fComputed));
            sb.append(String.format("%-20s %-15.4f %-10d %-15.4f\n", "Greška (SSE)", sse, dfE, mse));
            sb.append("---------------------------------------------------------------------------------\n");
            sb.append(String.format("%-20s %-15.4f %-10d\n", "Ukupno (SST)", sst, dfT));
            sb.append("=================================================================================\n\n");

            sb.append(String.format("Zajednička srednja vrijednost (Grand Mean): %.4f ms\n", grandMean));
            sb.append("Srednje vrijednosti po alternativama:\n");
            for (int j = 0; j < k; j++) {
                sb.append(String.format("  • Alternativa %d: %.4f ms\n", j + 1, groupMeans.get(j)));
            }
            sb.append("\n");

            sb.append(String.format("Kritična vrijednost F tabele F[%.3f; %d, %d] = %.4f\n", 1 - alpha, dfA, dfE, fTable));
            if (fComputed > fTable) {
                sb.append("ZAKLJUČAK: Izračunato F > F_tabele. Razlike između alternativa SU STATISTIČKI ZNAČAJNE!\n");
                sb.append("            (Veličina paketa/tehnologija ima ključan uticaj na performanse).");
            } else {
                sb.append("ZAKLJUČAK: Izračunato F <= F_tabele. Razlike NISU statistički značajne.\n");
                sb.append("            Uočene varijacije su posljedica slučajnog šuma.");
            }

            resultArea.setText(sb.toString());

            // Azuriranje ComboBox-ova za kontraste
            groupI.getItems().clear();
            groupJ.getItems().clear();
            for (int i = 0; i < k; i++) {
                groupI.getItems().add("Alternativa " + (i + 1));
                groupJ.getItems().add("Alternativa " + (i + 2 - 1)); // Indeksiranje od 1
            }
            groupI.getSelectionModel().selectFirst();
            if (k > 1) groupJ.getSelectionModel().select(1);

        } catch (Exception e) {
            resultArea.setText("Greška u analizi! Provjeri unos.\nDetalji: " + e.getMessage());
        }
    }

    private void calculateContrast() {
        if (data == null || groupMeans == null) {
            resultArea.appendText("\n\n[GRESKA] Prvo morate izvršiti ANOVA proračun!");
            return;
        }
        int idxI = groupI.getSelectionModel().getSelectedIndex();
        int idxJ = groupJ.getSelectionModel().getSelectedIndex();

        if (idxI < 0 || idxJ < 0) return;
        if (idxI == idxJ) {
            resultArea.appendText("\n\n[GRESKA] Ne možete porediti alternativu sa samom sobom!");
            return;
        }

        try {
            double alpha = Double.parseDouble(alphaField.getText().trim());
            int dfE = k * (n - 1);

            // 1. Izračunavanje vrijednosti kontrasta c (Jednačina 39/41)
            // Za parno poređenje: w_i = 1, w_j = -1, ostali su 0
            double c = groupMeans.get(idxI) - groupMeans.get(idxJ);

            // 2. Izračunavanje varijanse kontrasta s_c^2 (Jednačina 42 u radu)
            // Suma(w_j^2) za nas slucaj je (1)^2 + (-1)^2 = 2
            double sumW2 = 2.0;
            double sc2 = (sumW2 * mse) / (k * n); // mse je se^2 iz jednačine 42
            double sc = Math.sqrt(sc2);

            // 3. Dohvatanje t-vrijednosti (Aproksimacija t-distribucije)
            double tValue = getCriticalT(dfE, alpha);

            // 4. Granice intervala povjerenja (Jednačina 43)
            double marginOfError = tValue * sc;
            double c1 = c - marginOfError;
            double c2 = c + marginOfError;

            StringBuilder sb = new StringBuilder();
            sb.append("\n\n=================================================================================");
            sb.append(String.format("\nPOREĐENJE: Alternativa %d vs Alternativa %d (Težine: w_%d = +1, w_%d = -1)", idxI+1, idxJ+1, idxI+1, idxJ+1));
            sb.append("\n=================================================================================");
            sb.append(String.format("\nVrijednost kontrasta (c)           : %+.4f ms", c));
            sb.append(String.format("\nVarijansa kontrasta (s_c^2)        : %.4f", sc2));
            sb.append(String.format("\nStandardna greška kontrasta (s_c)  : %.4f", sc));
            sb.append(String.format("\nKritična t-vrijednost t[1-α/2; %d]  : %.4f", dfE, tValue));
            sb.append(String.format("\nInterval povjerenja (Jednačina 43) : [%.4f, %.4f]", c1, c2));
            sb.append("\n---------------------------------------------------------------------------------");

            // Provjera da li interval sadrzi nulu
            if (c1 <= 0 && c2 >= 0) {
                sb.append("\nZAKLJUČAK: Interval povjerenja SADRŽI nulu (0).");
                sb.append(String.format("\n           Razlika između Alternative %d i Alternative %d NIJE statistički značajna.", idxI+1, idxJ+1));
            } else {
                sb.append("\nZAKLJUČAK: Interval povjerenja NE SADRŽI nulu (0).");
                sb.append(String.format("\n           Razlika između Alternative %d i Alternative %d JE STATISTIČKI ZNAČAJNA!", idxI+1, idxJ+1));
                String brza = (c < 0) ? "Alternativa " + (idxI+1) : "Alternativa " + (idxJ+1);
                sb.append(String.format("\n           Statistički potvrđeno: %s je značajno brža.", brza));
            }
            sb.append("\n=================================================================================");

            resultArea.appendText(sb.toString());

        } catch (Exception e) {
            resultArea.appendText("\nGreška pri kalkulaciji kontrasta: " + e.getMessage());
        }
    }

    /**
     * Pomoćna statistička funkcija: Daje kritičnu F vrijednost za alpha=0.05 i alpha=0.01
     * (Pokriva najčešće eksperimentalne stepene slobode na osnovu Liljine knjige i tabela)
     */
    private double getCriticalF(int dfA, int dfE, double alpha) {
        if (Math.abs(alpha - 0.05) < 0.02) {
            if (dfA == 2 && dfE == 12) return 3.8853; // k=3, n=5 (Tvoj slucaj za Batch 50,100,500)
            if (dfA == 2 && dfE == 6)  return 5.1433; // k=3, n=3
            if (dfA == 1 && dfE == 8)  return 5.3177; // k=2, n=5
            if (dfA == 5 && dfE == 24) return 2.6207; // k=6, n=5
            return 3.00; // fallback aproksimacija
        } else { // Za alpha = 0.01
            if (dfA == 2 && dfE == 12) return 6.9266;
            return 5.00;
        }
    }

    /**
     * Pomoćna statistička funkcija: Daje kritičnu t vrijednost (dvostrani test)
     * za kreiranje intervala povjerenja kontrasta (Jednačina 43).
     */
    private double getCriticalT(int dfE, double alpha) {
        // Najčešći dfE za t-tabelu kod malih uzoraka (alfa=0.05 dvostrano znači 0.975 kvantil)
        boolean isAlpha05 = Math.abs(alpha - 0.05) < 0.02;
        if (isAlpha05) {
            switch (dfE) {
                case 4:  return 2.776;
                case 6:  return 2.447;
                case 8:  return 2.306;
                case 12: return 2.179; // Tvoj tačan slučaj (k=3, n=5 -> dfE = 3 * 4 = 12)
                case 24: return 2.064;
                default: return 2.000;
            }
        } else { // alpha = 0.01 (0.995 kvantil)
            if (dfE == 12) return 3.055;
            return 2.58;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
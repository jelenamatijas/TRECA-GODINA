module anovaApp {
    requires javafx.controls;
    requires javafx.fxml;

    opens jelena.etfbl to javafx.graphics, javafx.fxml;
}
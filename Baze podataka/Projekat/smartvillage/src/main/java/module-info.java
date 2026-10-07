module jelena.etfbl.smartvillage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens jelena.etfbl.smartvillage.app to javafx.fxml;
    exports jelena.etfbl.smartvillage.app;

    exports jelena.etfbl.smartvillage.database;
    exports jelena.etfbl.smartvillage.pomocneKlase;
    opens jelena.etfbl.smartvillage.pomocneKlase to javafx.fxml;
}
module jelena.etfbl.evoting {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jfr;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
    requires spring.security.crypto;
    requires jdk.compiler;
    requires mysql.connector.j;


    opens jelena.etfbl.evoting to javafx.fxml;
    exports jelena.etfbl.evoting;
}
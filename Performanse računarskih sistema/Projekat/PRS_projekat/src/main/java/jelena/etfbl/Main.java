package jelena.etfbl;

import com.formdev.flatlaf.FlatDarkLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;

/*freesqldatabase.com: jelena.matijas@student.etf.unibl.org - password
*
https://console.aiven.io/account/a5b4df24d696/project/prs-2026/services/ - 8bIxyZX2UBxFZBcSw5AIc9iyYiZY5sPG
*/

/* ************ KONEKCIONI PODACI ************
* MYSQL: 3306, root, localhost, dbtest, password
* MYSQL: 3306, sql7827118, sql7.freesqldatabase.com, sql7827118, kHtaKzJKea
* POSTGRESQL: 5432, postgres, localhost, dbtest, password
* POSTGRESQL: 15963, avnadmin, postgres-prs-2026.j.aivencloud.com, defaultdb, AVNS_cuCEPiulussFQaffIkh */


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Database Test Data Filler");
        try {
            System.out.println("start");
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            log.warn("Could not set FlatLaf theme, using default", e);
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}
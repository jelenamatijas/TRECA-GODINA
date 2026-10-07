package jelena.etfbl.evoting.model;

import jelena.etfbl.evoting.crypto.CertificateUtils;
import jelena.etfbl.evoting.crypto.PasswordUtils;
import jelena.etfbl.evoting.database.UserDAO;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class RegistrationService {
    private static final String USER_KEYS_DIR = "user_keys";
    private static final String CA_PASSWORD = "sigurnost";

    public static boolean registrationOrganizer(String username, String password, String orgName, String idNumber) throws Exception{
        if(UserDAO.existsByUsername(username)){
            System.err.println("Postoji korisnik sa unesenim korisnickim imenom");
            return false;
        }

        ensureUserKeyDirectoryExists();
        KeyPair userKeyPair = CertificateUtils.generateRSAKeyPair();
        PrivateKey orgCaKey = CertificateUtils.loadPrivateKey("pki/org_ca.p12", "org_ca", CA_PASSWORD);
        X509Certificate orgCaCert = CertificateUtils.loadCertificate("pki/org_ca.p12", "org_ca", CA_PASSWORD);
        String dn = "CN=" + username + ", O=" + orgName + ", C=BA";
        X509Certificate userCert = CertificateUtils.createUserCertificate(dn, userKeyPair.getPublic(), orgCaKey, orgCaCert, true);

        String keyStorePath = USER_KEYS_DIR + "/" + username + ".p12";
        CertificateUtils.saveKeyStore(keyStorePath, username, userKeyPair.getPrivate(), userCert, orgCaCert, password);

        String passwordHash = PasswordUtils.hashPassword(password);
        String certSerial = userCert.getSerialNumber().toString();

        UserDAO.registerOrganizer(username, passwordHash, orgName, idNumber, certSerial);
        CertificateUtils.exportToCRT(userCert, "user_keys/" + username + ".crt");
        System.out.println("Organizator " + username + " uspjesno kreiran, a njegov sertifikat je na putanji: " + keyStorePath);
        return true;
    }

    private static void ensureUserKeyDirectoryExists(){
        File dir = new File(USER_KEYS_DIR);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    public static boolean registerVoter(String username, String password, String firstName, String lastName)throws Exception{
        if(UserDAO.existsByUsername(username)){
            System.err.println("Postoji korisnik sa unesenim korisnickim imenom");
            return false;
        }

        ensureUserKeyDirectoryExists();
        KeyPair userKeyPair = CertificateUtils.generateRSAKeyPair();
        PrivateKey voterCaKey = CertificateUtils.loadPrivateKey("pki/voter_ca.p12", "voter_ca", CA_PASSWORD);
        X509Certificate voterCaCert = CertificateUtils.loadCertificate("pki/voter_ca.p12", "voter_ca", CA_PASSWORD);
        String dn = "CN=" + username + ", O=ETF BL, C=BA";
        X509Certificate userCert = CertificateUtils.createUserCertificate(dn, userKeyPair.getPublic(), voterCaKey, voterCaCert, false);

        String keyStorePath = USER_KEYS_DIR + "/" + username + ".p12";
        CertificateUtils.saveKeyStore(keyStorePath, username, userKeyPair.getPrivate(), userCert, voterCaCert, password);
        CertificateUtils.exportToCRT(userCert, USER_KEYS_DIR + "/" + username + ".crt");
        String passwordHash = PasswordUtils.hashPassword(password);
        String certSerial = userCert.getSerialNumber().toString();

        UserDAO.registerVoter(username, passwordHash, firstName, lastName, certSerial);
        System.out.println("Glasac " + username + " uspjesno kreiran, a njegov sertifikat je na putanji: " + keyStorePath);
        return true;
    }
}

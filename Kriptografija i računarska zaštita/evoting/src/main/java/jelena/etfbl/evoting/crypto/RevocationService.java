package jelena.etfbl.evoting.crypto;

import jelena.etfbl.evoting.database.UserDAO;
import jelena.etfbl.evoting.model.User;
import jelena.etfbl.evoting.model.UserRole;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class RevocationService {
    private static final String CA_PASSWORD = "sigurnost";

    public static boolean revokeUser(String usernameToRevoke)throws Exception{
        User user = UserDAO.findByUsername(usernameToRevoke);
        if(user == null){
            System.err.println("Korisnik pod imenom " + usernameToRevoke + " ne postoji.");
            return false;
        }

        UserDAO.markAsRevoked(user.getUserId());

        String caP12Path = (user.getUserRole() == UserRole.ORGANIZATOR) ? "pki/org_ca.p12" : "pki/voter_ca.p12";
        String alias = (user.getUserRole() == UserRole.ORGANIZATOR) ? "org_ca" : "voter_ca";
        String crlPath = (user.getUserRole() == UserRole.ORGANIZATOR) ? "pki/org_ca.crl" : "pki/voter_ca.crl";

        PrivateKey caPrivateKey = CertificateUtils.loadPrivateKey(caP12Path, alias, CA_PASSWORD);
        X509Certificate caCert = CertificateUtils.loadCertificate(caP12Path, alias, CA_PASSWORD);

        BigInteger certSerial = new BigInteger(user.getCertificateSerialNumber());
        CertificateUtils.revokeCertificate(certSerial, caCert, caPrivateKey, crlPath);

        System.out.println("Korisnik " + usernameToRevoke + " je uspjesno opozvan i njegov sertifikaat je dodan na CRL listu.");
        return true;
    }
}

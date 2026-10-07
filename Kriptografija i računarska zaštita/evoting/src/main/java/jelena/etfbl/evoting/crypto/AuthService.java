package jelena.etfbl.evoting.crypto;

import jelena.etfbl.evoting.database.UserDAO;
import jelena.etfbl.evoting.model.User;
import jelena.etfbl.evoting.model.UserRole;

import java.security.cert.X509Certificate;

public class AuthService {
    public static User login(String username, String password, String p12FilePAth)throws Exception{
        User user = UserDAO.findByUsername(username);
        if(user == null){
            System.err.println("Korisnik ne postoji.");
            return null;
        }

        if(user.isRevoked() || user.getFailedAttempts() >= 3){
            System.err.println("Nalog je blokiran zbog previse neuspjesnih pokusaja.");
            return null;
        }

        if(!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            UserDAO.incrementFailedAttempts(user.getUserId());
            int newAttemps = user.getFailedAttempts() + 1;
            if (newAttemps >= 3) {
                RevocationService.revokeUser(username);
                System.err.println("Nalog je trenutno blokiran zbog tri neuspjesna pokusaja prijavljivanja.");
            } else {
                System.err.println("Pogresna lozinka! Pokusaj: " + newAttemps + "/3");
            }
            return null;
        }

        X509Certificate userCert = CertificateUtils.loadCertificate(p12FilePAth, username, password);

        try{
            userCert.checkValidity();
        }catch (Exception e){
            System.err.println("Sertifikat nije vremenski validan!");
            return null;
        }

        String subjectDN = userCert.getSubjectX500Principal().getName();
        if(!subjectDN.contains("CN=" + username)){
            System.err.println("Sertifikat ne pripada korisniku " + username);
            return null;
        }

        String caP12Pth = (user.getUserRole() == UserRole.ORGANIZATOR) ? "pki/org_ca.p12" : "pki/voter_ca.p12";
        String caAlias = (user.getUserRole() == UserRole.ORGANIZATOR) ? "org_ca" : "voter_ca";
        String crlFilePath = (user.getUserRole() == UserRole.ORGANIZATOR) ? "pki/org_ca.crl" : "pki/voter_ca.crl";

        X509Certificate caCert = CertificateUtils.loadCertificate(caP12Pth, caAlias, "sigurnost");
        try{
            userCert.verify(caCert.getPublicKey(), "BC");
        }catch (Exception e){
            System.err.println("Detalji greške pri verifikaciji: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        if(CertificateUtils.isCertificateRevoked(userCert, crlFilePath)){
            UserDAO.markAsRevoked(user.getUserId());
            System.err.println("Sertifikat korisnika je opozvan na CRL listi!");
            return null;
        }

        System.err.println("Uspjesna prijava korisnika: " + username);
        return user;
    }
}

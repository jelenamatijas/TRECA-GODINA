package jelena.etfbl.evoting.crypto;

import java.io.File;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

public class PKIInitializer {
    private static final String PKI_DIR = "pki";
    private static final String CA_PASSWORD = "sigurnost";

    public static void main(String[] args){
        try{
            File dir = new File(PKI_DIR);
            if(!dir.exists()){
                dir.mkdir();
            }

            System.out.println("Pocinje inicijalizacija PKI infrastrukture");

            KeyPair rootKeyPair = CertificateUtils.generateRSAKeyPair();

            X509Certificate rootCertificate = CertificateUtils.createCACertificate("CN=Root CA, O=ETF BL, C=BA", rootKeyPair, rootKeyPair.getPrivate(), null, 1);
            X509CRL rootCRL = CertificateUtils.createEmptyCRL(rootCertificate, rootKeyPair.getPrivate());
            CertificateUtils.saveKeyStore(PKI_DIR + "/root_ca.p12", "root_ca", rootKeyPair.getPrivate(), rootCertificate, null, CA_PASSWORD);
            CertificateUtils.saveCRL(PKI_DIR + "/root_ca.crl", rootCRL);
            System.out.println("ROOT CA kreiran.");

            KeyPair orgKeyPair = CertificateUtils.generateRSAKeyPair();
            X509Certificate orgCertificate = CertificateUtils.createCACertificate("CN=Organizaciono CA, O=ETF BL, C=BA", orgKeyPair, rootKeyPair.getPrivate(), rootCertificate, 0);
            X509CRL orgCRL = CertificateUtils.createEmptyCRL(orgCertificate, orgKeyPair.getPrivate());
            CertificateUtils.saveKeyStore(PKI_DIR + "/org_ca.p12", "org_ca", orgKeyPair.getPrivate(), orgCertificate, rootCertificate, CA_PASSWORD);
            CertificateUtils.saveCRL(PKI_DIR + "/org_ca.crl", orgCRL);
            System.out.println("ORGANIZACIONO CA kreiran.");

            KeyPair voterKeyPair = CertificateUtils.generateRSAKeyPair();
            X509Certificate voterCertificate = CertificateUtils.createCACertificate("CN=Glasacko CA, O=ETF BL, C=BA", voterKeyPair, rootKeyPair.getPrivate(), rootCertificate, 0);
            X509CRL voterCRL = CertificateUtils.createEmptyCRL(voterCertificate, voterKeyPair.getPrivate());
            CertificateUtils.saveKeyStore(PKI_DIR + "/voter_ca.p12", "voter_ca", voterKeyPair.getPrivate(), voterCertificate, rootCertificate, CA_PASSWORD);
            CertificateUtils.saveCRL(PKI_DIR + "/voter_ca.crl", voterCRL);
            System.out.println("GLASACKO CA kreiran.");
        } catch (Exception e) {
            System.err.println("Greksa pri inicijalizaciji: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

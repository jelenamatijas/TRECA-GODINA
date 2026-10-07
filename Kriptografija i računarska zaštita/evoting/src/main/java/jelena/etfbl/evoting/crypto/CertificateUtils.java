package jelena.etfbl.evoting.crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.asn1.x509.CRLReason;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;

public class CertificateUtils {
    static{
        if(Security.getProvider("BC") == null){
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static KeyPair generateRSAKeyPair() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static X509Certificate createCACertificate(
            String disName,
            KeyPair keyPair,
            PrivateKey privateKey,
            X509Certificate cert,
            int pathLenConstraint
            ) throws Exception{
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + 10L * 365 * 24 * 3600);
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X500Name subjectDN = new X500Name(disName);

        X500Name issuerDN = (cert == null) ? subjectDN : new X500Name(cert.getSubjectX500Principal().getName());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(  issuerDN,
                                                                                    serialNumber,
                                                                                    startDate,
                                                                                    endDate,
                                                                                    subjectDN,
                                                                                    keyPair.getPublic());
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(pathLenConstraint));
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
    }

    public static X509CRL createEmptyCRL(X509Certificate caCert, PrivateKey caPrivateKey) throws Exception{
        Date today = new Date();
        Date nextUpdate = new Date(today.getTime() + 365L * 24 * 3600 * 1000L);

        JcaX509v2CRLBuilder crlBuilder = new JcaX509v2CRLBuilder(caCert, today);
        crlBuilder.setNextUpdate(nextUpdate);

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caPrivateKey);
        X509CRLHolder crlHolder = crlBuilder.build(signer);

        return new JcaX509CRLConverter().setProvider("BC").getCRL(crlHolder);
    }

    public static void saveKeyStore(String filePath,
                                    String alias,
                                    PrivateKey privateKey,
                                    X509Certificate cert,
                                    X509Certificate issuerCert,
                                    String password) throws  Exception{
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, null);

        X509Certificate[] chain;
        if(issuerCert != null){
            chain = new X509Certificate[]{cert, issuerCert};
        }else{
            chain = new X509Certificate[]{cert};
        }

        keyStore.setKeyEntry(alias, privateKey, password.toCharArray(), chain);

        try(FileOutputStream out = new FileOutputStream(filePath)){
            keyStore.store(out, password.toCharArray());
        }
    }

    public static void saveCRL(String filePath, X509CRL crl) throws Exception{
        try(FileOutputStream out = new FileOutputStream(filePath)){
            out.write(crl.getEncoded());
        }
    }

    public static X509Certificate createUserCertificate(String dn, PublicKey userPublicKey, PrivateKey caPrivateKey, X509Certificate caCert, boolean isOrganizer) throws Exception{
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 1L * 365 * 24 * 3600 * 1000L);

        BigInteger serialNumber = BigInteger.valueOf(System.nanoTime());
        X500Name subjectDN = new X500Name(dn);
        X500Name issuerDN = new X500Name(caCert.getSubjectX500Principal().getName());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerDN, serialNumber, startDate, endDate, subjectDN, userPublicKey);
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        if(isOrganizer){
            certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage((KeyUsage.digitalSignature | KeyUsage.keyEncipherment)));
        }else{
            certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        }

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caPrivateKey);
        X509CertificateHolder certHolder = certBuilder.build(signer);

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
    }

    public static PrivateKey loadPrivateKey(String filePath, String alias, String password)throws Exception{
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        try(FileInputStream in = new FileInputStream(filePath)){
            keyStore.load(in, password.toCharArray());
        }
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    public static X509Certificate loadCertificate(String filePath, String alias, String password)throws Exception{
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        try(FileInputStream in = new FileInputStream(filePath)){
            keyStore.load(in, password.toCharArray());
        }
        return (X509Certificate) keyStore.getCertificate(alias);
    }

    public static boolean isCertificateRevoked(X509Certificate cert, String crlFilePath) throws Exception{
        File crlFile = new File(crlFilePath);
        if (!crlFile.exists()) {
            return false;
        }
        try(FileInputStream in = new FileInputStream(crlFilePath)){
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            X509CRL crl = (X509CRL)cf.generateCRL(in);
            return crl.isRevoked(cert);
        }
    }

    public static void revokeCertificate(BigInteger serialNumber, X509Certificate caCert, PrivateKey caPrivateKey, String crlPath)throws Exception{
        JcaX509v2CRLBuilder crlBuilder = new JcaX509v2CRLBuilder(caCert, new Date());
        crlBuilder.setNextUpdate(new Date(System.currentTimeMillis() + 365L * 24 * 3600));

        File crlFile = new File(crlPath);
        if(crlFile.exists()){
            try(FileInputStream in = new FileInputStream(crlFile)){
                CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
                X509CRL existingCrl = (X509CRL) cf.generateCRL(in);
                if(existingCrl.getRevokedCertificates() != null){
                    for(X509CRLEntry entry: existingCrl.getRevokedCertificates()){
                        crlBuilder.addCRLEntry(entry.getSerialNumber(), entry.getRevocationDate(), CRLReason.unspecified);
                    }
                }
            }
        }

        crlBuilder.addCRLEntry(serialNumber, new Date(), CRLReason.unspecified);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(caPrivateKey);
        X509CRLHolder crlHolder = crlBuilder.build(signer);
        X509CRL newCrl = new JcaX509CRLConverter().setProvider("BC").getCRL(crlHolder);
        try(FileOutputStream out = new FileOutputStream(crlPath)){
            out.write(newCrl.getEncoded());
        }
    }

    public static X509Certificate loadPublicCertificate(String crtFilePath) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(crtFilePath)) {
            java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509", "BC");
            return (java.security.cert.X509Certificate) cf.generateCertificate(fis);
        }
    }

    public static void exportToCRT(X509Certificate cert, String filePath) throws Exception {
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            fos.write(cert.getEncoded());
        }
    }
}

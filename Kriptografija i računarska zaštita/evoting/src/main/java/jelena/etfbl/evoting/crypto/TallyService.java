package jelena.etfbl.evoting.crypto;

import jelena.etfbl.evoting.database.UserDAO;
import jelena.etfbl.evoting.database.VotingDAO;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.*;

public class TallyService {
    private static final int IV_LENGTH = 16;

    public static Map<String, Integer> tallyVotes(int votingId, String orgUsername, String orgPassword)throws  Exception
    {
        String actualOrganizer = VotingDAO.getOrganizerUsernameForVoting(votingId);
        if (actualOrganizer == null) {
            throw new IllegalArgumentException("Glasanje sa ID " + votingId + " ne postoji.");
        }
        if (!actualOrganizer.equals(orgUsername)) {
            throw new SecurityException("Korisnik " + orgUsername + " nije organizator glasanja " + votingId + " i ne moze pokrenuti brojanje.");
        }

        if (!VotingDAO.isVotingEnded(votingId)) {
            throw new IllegalStateException("Glasanje jos nije zavrseno - brojanje glasova jos nije dozvoljeno.");
        }

        PrivateKey orgPrivateKey = CertificateUtils.loadPrivateKey("user_keys/" + orgUsername + ".p12", orgUsername, orgPassword);
        List<VotingDAO.EncryptedVoteRecord> records = VotingDAO.getEncryptedVotesForVoting(votingId);
        List<String> voterUsernames = UserDAO.getAllVoterUsernames();
        System.out.println("Pronadjeno ukupno " + records.size() + " u bazi za glasanje ID = " + votingId);

        Map<String, Integer> results = new HashMap<>();
        int validVotes = 0;
        int invalidVotes = 0;

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5padding", "BC");

        for(VotingDAO.EncryptedVoteRecord record: records) {
            try {
                byte[] encryptedVoteBytes = Base64.getDecoder().decode(record.encryptedVote);
                byte[] encryptedSymKeyBytes = Base64.getDecoder().decode(record.encryptedSymKey);
                byte[] voterSigBytes = Base64.getDecoder().decode(record.voterSignature);

                boolean signatureVerified = false;
                for(String voterUsername : voterUsernames){
                    try{
                        X509Certificate voterCert = CertificateUtils.loadPublicCertificate("user_keys/" + voterUsername + ".crt");
                        PublicKey voterPublicKey = voterCert.getPublicKey();
                        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
                        signature.initVerify(voterPublicKey);
                        signature.update(encryptedVoteBytes);

                        if (signature.verify(voterSigBytes)) {
                            signatureVerified = true;
                            break;
                        }
                    }catch (Exception ignored){

                    }
                }

                if (!signatureVerified) {
                    System.err.println("Digitalni potpis nad glasom nije validan. Glas se odbacuje.");
                    invalidVotes++;
                    continue;
                }

                rsaCipher.init(Cipher.DECRYPT_MODE, orgPrivateKey);
                byte[] aesKeyBytes = rsaCipher.doFinal(encryptedSymKeyBytes);
                SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                byte[] iv = Arrays.copyOfRange(encryptedVoteBytes, 0, IV_LENGTH);
                byte[] cipherText = Arrays.copyOfRange(encryptedVoteBytes, IV_LENGTH, encryptedVoteBytes.length);

                aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
                byte[] decryptedVoteBytes = aesCipher.doFinal(cipherText);
                String selectedOption = new String(decryptedVoteBytes);

                results.put(selectedOption, results.getOrDefault(selectedOption, 0) + 1);
                validVotes++;
            } catch (Exception e) {
                System.out.println("Greska pri desifrovanju glasa: " + e.getMessage());
                invalidVotes++;
            }
        }
        System.out.println("\n === Rezultati glasanja ===");
        System.out.println("Ukupno validnih glasova: " + validVotes);
        System.out.println("Ukupno nevazecih glasova: " + invalidVotes);
        results.forEach((option, count) -> System.out.println(option + ": " + count + " glas(a)"));
        generateAndSignReport(votingId, orgUsername, orgPrivateKey, results, validVotes, invalidVotes);
        return results;
    }

    private static void generateAndSignReport(int votingId, String orgUsername, PrivateKey orgPrivateKey,
                                              Map<String, Integer> results, int validVotes, int invalidVotes) throws Exception {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("=== ZAVRŠNI IZVJEŠTAJ GLASANJA ===\n");
        reportBuilder.append("ID Glasanja: ").append(votingId).append("\n");
        reportBuilder.append("Organizator: ").append(orgUsername).append("\n");
        reportBuilder.append("Datum: ").append(new Timestamp(System.currentTimeMillis())).append("\n");
        reportBuilder.append("Važećih glasova: ").append(validVotes).append("\n");
        reportBuilder.append("Nevažećih glasova: ").append(invalidVotes).append("\n\n");
        reportBuilder.append("REZULTATI:\n");

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            reportBuilder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" glas(a)\n");
        }

        byte[] reportData = reportBuilder.toString().getBytes(StandardCharsets.UTF_8);

        Signature signer = Signature.getInstance("SHA256withRSA", "BC");
        signer.initSign(orgPrivateKey);
        signer.update(reportData);
        byte[] reportSignature = signer.sign();

        File dir = new File("reports");
        if (!dir.exists()) dir.mkdirs();

        String reportPath = "reports/izvjestaj_" + votingId + ".txt";
        String sigPath = "reports/izvjestaj_" + votingId + ".sig";

        try (FileOutputStream fos = new FileOutputStream(reportPath)) {
            fos.write(reportData);
        }
        try (FileOutputStream fos = new FileOutputStream(sigPath)) {
            fos.write(Base64.getEncoder().encode(reportSignature));
        }

        System.out.println("\nDigitalno potpisan izvještaj je uspješno kreiran u direktorijumu 'reports/'!");
    }


}


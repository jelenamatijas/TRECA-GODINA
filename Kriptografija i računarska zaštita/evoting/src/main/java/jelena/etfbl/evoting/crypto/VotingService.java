package jelena.etfbl.evoting.crypto;

import jelena.etfbl.evoting.database.VotingDAO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.*;

public class VotingService {
    private static final String HMAC_SECRET_KEY = "sigurnost";
    private static final int IV_LENGTH = 16;

    public static int createVoting(int organizerId, String title, String description,
                                   Timestamp startTime, Timestamp endTime, List<String> options) throws Exception {
        if (options == null || options.size() < 2 || options.size() > 5) {
            throw new IllegalArgumentException("Glasanje mora sadrzati izmedju 2 i 5 ponudjenih opcija.");
        }
        if (startTime == null || endTime == null || !endTime.after(startTime)) {
            throw new IllegalArgumentException("Vrijeme zavrsetka glasanja mora biti nakon vremena pocetka.");
        }
        if (endTime.before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("Vrijeme zavrsetka glasanja ne moze biti u proslosti.");
        }
        return VotingDAO.createVoting(organizerId, title, description, startTime, endTime, options);
    }

    public static List<VotingDAO.VotingSummary> getVotingsForOrganizer(int organizerId) throws Exception {
        return VotingDAO.getAllVotingsForOrganizer(organizerId);
    }

    public static List<VotingDAO.VotingSummary> getActiveVotingsForVoter() throws Exception {
        return VotingDAO.getActiveVotingsWithOptions();
    }

    public static String castVote(int votingId, int voterId, String username, String password, String selectedOption, X509Certificate organizerCert) throws Exception {
        VotingDAO.VotingSummary voting = VotingDAO.getVotingDetails(votingId);
        if (voting == null) {
            System.err.println("Glasanje sa ID " + votingId + " ne postoji.");
            return null;
        }
        if (!VotingDAO.isVotingActive(votingId)) {
            System.err.println("Glasanje trenutno nije aktivno (jos nije pocelo ili je vec zavrseno).");
            return null;
        }
        if (voting.options == null || !voting.options.contains(selectedOption)) {
            System.err.println("Izabrana opcija ne pripada ponudjenim kandidatima za ovo glasanje.");
            return null;
        }
        if (VotingDAO.hasVoterVoted(votingId, voterId)) {
            System.err.println("Korisnik je vec glasao na ovom glasanju.");
            return null;
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BC");
        keyGenerator.init(256);
        SecretKey aesKey = keyGenerator.generateKey();

        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] cipherText  = aesCipher.doFinal(selectedOption.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedVote = new byte[IV_LENGTH + cipherText.length];
        System.arraycopy(iv, 0, encryptedVote, 0, IV_LENGTH);
        System.arraycopy(cipherText, 0, encryptedVote, IV_LENGTH, cipherText.length);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsaCipher.init(Cipher.ENCRYPT_MODE, organizerCert.getPublicKey());
        byte[] encryptedSymKey = rsaCipher.doFinal(aesKey.getEncoded());

        PrivateKey voterPrivateKey = CertificateUtils.loadPrivateKey("user_keys/" + username + ".p12", username, password);
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
        signature.initSign(voterPrivateKey);
        signature.update(encryptedVote);
        byte[] voterSignature = signature.sign();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] receiptHashBytes = md.digest(encryptedVote);
        String voteReceiptHash = Base64.getEncoder().encodeToString(receiptHashBytes);

        VotingDAO.saveEncryptedVote(votingId, encryptedVote, encryptedSymKey, voterSignature, voteReceiptHash);

        Timestamp now = new Timestamp((System.currentTimeMillis() / 1000L) * 1000L);
        String metadataContent = votingId + ":" + voterId + ":" + now.toString();

        Mac hmac = Mac.getInstance("HmacSHA256", "BC");
        SecretKeySpec hmacKeySpec = new SecretKeySpec(HMAC_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(hmacKeySpec);
        String hmacSignature = Base64.getEncoder().encodeToString(hmac.doFinal(metadataContent.getBytes(StandardCharsets.UTF_8)));
        VotingDAO.saveVoteMetadata(votingId, voterId, now, hmacSignature);

        System.out.println("Glas je uspjesno enkriptovan, potpisan i zapisan u bazu.");
        System.out.println("Vasa potvrda (receipt) glasa: " + voteReceiptHash);
        System.out.println("Sacuvajte ovaj kod - njime mozete provjeriti da je vas glas zabiljezen, bez otkrivanja sadrzaja.");

        return voteReceiptHash;
    }

    public static boolean verifyVote(String voteReceiptHash) throws Exception {
        if (voteReceiptHash == null || voteReceiptHash.isBlank()) {
            return false;
        }
        return VotingDAO.voteReceiptExists(voteReceiptHash);
    }

    public static List<Integer> verifyMetadataIntegrity(int votingId) throws Exception {
        List<Integer> corrupted = new ArrayList<>();
        List<VotingDAO.VoteMetadataRecord> records = VotingDAO.getMetadataForVoting(votingId);

        Mac hmac = Mac.getInstance("HmacSHA256", "BC");
        SecretKeySpec hmacKeySpec = new SecretKeySpec(HMAC_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        for (VotingDAO.VoteMetadataRecord record : records) {
            hmac.init(hmacKeySpec);
            String metadataContent = votingId + ":" + record.voterId + ":" + record.votingTimestamp.toString();
            String expected = Base64.getEncoder().encodeToString(hmac.doFinal(metadataContent.getBytes(StandardCharsets.UTF_8)));
            if (!expected.equals(record.hmacSignature)) {
                corrupted.add(record.voterId);
            }
        }
        return corrupted;
    }

    @Deprecated
    public static Map<String, Integer> decryptAndCountVotes(int votingId, String orgUsername, String orgPassword) throws Exception {
        return TallyService.tallyVotes(votingId, orgUsername, orgPassword);
    }
}

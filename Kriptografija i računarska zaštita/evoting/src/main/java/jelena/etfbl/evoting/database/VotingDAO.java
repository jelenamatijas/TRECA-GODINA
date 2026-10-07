package jelena.etfbl.evoting.database;


import com.mysql.cj.xdevapi.StreamingSqlResultBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class VotingDAO {

    private static String escapeJsonString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String encodeOptions(List<String> options) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJsonString(options.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    public static List<String> decodeOptions(String optionsRaw) {
        List<String> result = new ArrayList<>();
        if (optionsRaw == null || optionsRaw.isBlank()) {
            return result;
        }
        String trimmed = optionsRaw.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        if (trimmed.isEmpty()) {
            return result;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean escapeNext = false;

        for (char c : trimmed.toCharArray()) {
            if (escapeNext) {
                current.append(c);
                escapeNext = false;
                continue;
            }
            if (c == '\\') {
                escapeNext = true;
                continue;
            }
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) {
            result.add(current.toString().trim());
        }
        return result;
    }

    public static int createVoting(int organizerId, String title, String description, Timestamp startTime, Timestamp endTime, List<String> options)throws SQLException{
        if(options == null || options.size() < 2 || options.size()>5){
            throw new IllegalArgumentException("Glasanje mora imati izmedju 2 i 5 kandidata.");
        }

        if(startTime == null || endTime == null || !endTime.after(startTime)){
            throw new IllegalArgumentException("Vrijeme zavrsetka glasanja mora biti nakon njegovog pocetka.");
        }

        return createVoting(organizerId, title, description, startTime, endTime, encodeOptions(options));
    }

    public static int createVoting(int organizerId, String title, String description, Timestamp startTime, Timestamp endTime, String optionsJson)throws SQLException{
        String sql = "insert into voting (organizer_id, title, voting_description, start_time, end_time, options_json) values (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, organizerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setTimestamp(4, startTime);
            ps.setTimestamp(5, endTime);
            ps.setString(6, optionsJson);

            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static String getOrganizerUsernameForVoting(int votingId) throws SQLException {
        String sql = "SELECT u.username FROM voting v JOIN voting_user u ON v.organizer_id = u.user_id WHERE v.voting_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

    public static void saveVoteMetadata(int votingId, int voterId, Timestamp votingTimestamp, String hmacSignature)throws SQLException{
        String sql = "insert into vote_metadata (voting_id, voter_id, voting_timestamp, hmac_signature) values (?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, votingId);
            ps.setInt(2, voterId);
            ps.setTimestamp(3, votingTimestamp);
            ps.setString(4, hmacSignature);
            ps.executeUpdate();
        }
    }

    public static void saveEncryptedVote(int votingId, byte[] encryptedVote, byte[]encryptedSymKey, byte[] voterSignature, String voteReceiptHash)throws SQLException{
        String sql = "insert into encrypted_votes (voting_id, encrypted_vote, encrypted_sym_key, voter_signature, vote_receipt_hash) values (?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, votingId);
            ps.setString(2, Base64.getEncoder().encodeToString(encryptedVote));
            ps.setString(3, Base64.getEncoder().encodeToString(encryptedSymKey));
            ps.setString(4, Base64.getEncoder().encodeToString(voterSignature));
            ps.setString(5, voteReceiptHash);

            ps.executeUpdate();
        }
    }

    public static List<String> getActiveVoting()throws SQLException{
        List<String> list = new ArrayList<>();
        String sql = "select voting_id, title, voting_description from voting where end_time > ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add("ID: " + rs.getInt("voting_id") + " | " + rs.getString("title") + " (" + rs.getString("voting_description") + ")");
                }
            }
        }
        return list;
    }

    public static List<EncryptedVoteRecord> getEncryptedVotesForVoting(int votingId)throws SQLException{
        List<EncryptedVoteRecord> list = new ArrayList<>();
        String sql = "select encrypted_vote, encrypted_sym_key, voter_signature, vote_receipt_hash from encrypted_votes where voting_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, votingId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(new EncryptedVoteRecord(
                            rs.getString("encrypted_vote"),
                            rs.getString("encrypted_sym_key"),
                            rs.getString("voter_signature"),
                            rs.getString("vote_receipt_hash")
                    ));
                }
            }
        }
        return list;
    }

    public static class EncryptedVoteRecord{
        public String encryptedVote;
        public String encryptedSymKey;
        public String voterSignature;
        public String voteReceiptHash;

        public EncryptedVoteRecord(String encryptedVote, String encryptedSymKey, String voterSignature, String voteReceiptHash){
            this.encryptedVote = encryptedVote;
            this.encryptedSymKey = encryptedSymKey;
            this.voterSignature = voterSignature;
            this.voteReceiptHash = voteReceiptHash;
        }
    }

    public static class VotingSummary {
        public int votingId;
        public int organizerId;
        public String title;
        public String description;
        public Timestamp startTime;
        public Timestamp endTime;
        public List<String> options;
        public String status;

        public VotingSummary(int votingId, int organizerId, String title, String description,
                             Timestamp startTime, Timestamp endTime, List<String> options) {
            this.votingId = votingId;
            this.organizerId = organizerId;
            this.title = title;
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
            this.options = options;

            long now = System.currentTimeMillis();
            if (startTime != null && now < startTime.getTime()) {
                this.status = "NIJE_POCELO";
            } else if (endTime != null && now > endTime.getTime()) {
                this.status = "ZAVRSENO";
            } else {
                this.status = "AKTIVNO";
            }
        }

        @Override
        public String toString() {
            return "ID: " + votingId + " | " + title + " | " + status +
                    " | pocetak: " + startTime + " | kraj: " + endTime +
                    " | kandidati: " + options;
        }
    }

    private static VotingSummary mapRow(ResultSet rs) throws SQLException {
        return new VotingSummary(
                rs.getInt("voting_id"),
                rs.getInt("organizer_id"),
                rs.getString("title"),
                rs.getString("voting_description"),
                rs.getTimestamp("start_time"),
                rs.getTimestamp("end_time"),
                decodeOptions(rs.getString("options_json"))
        );
    }

    public static List<VotingSummary> getAllVotingsForOrganizer(int organizerId) throws SQLException {
        List<VotingSummary> list = new ArrayList<>();
        String sql = "select voting_id, organizer_id, title, voting_description, start_time, end_time, options_json " +
                "from voting where organizer_id = ? order by start_time desc";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, organizerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public static VotingSummary getVotingDetails(int votingId) throws SQLException {
        String sql = "select voting_id, organizer_id, title, voting_description, start_time, end_time, options_json from voting where voting_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public static List<VotingSummary> getActiveVotingsWithOptions() throws SQLException {
        List<VotingSummary> list = new ArrayList<>();
        String sql = "select voting_id, organizer_id, title, voting_description, start_time, end_time, options_json " +
                "from voting where start_time <= ? and end_time > ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, now);
            ps.setTimestamp(2, now);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public static boolean isVotingActive(int votingId) throws SQLException {
        String sql = "select count(*) from voting where voting_id = ? and start_time <= ? and end_time > ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            ps.setTimestamp(2, now);
            ps.setTimestamp(3, now);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean isVotingEnded(int votingId) throws SQLException {
        String sql = "select count(*) from voting where voting_id = ? and end_time <= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean hasVoterVoted(int votingId, int voterId) throws SQLException {
        String sql = "select count(*) from vote_metadata where voting_id = ? and voter_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            ps.setInt(2, voterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean voteReceiptExists(String voteReceiptHash) throws SQLException {
        String sql = "select count(*) from encrypted_votes where vote_receipt_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, voteReceiptHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static class VoteMetadataRecord {
        public int voterId;
        public Timestamp votingTimestamp;
        public String hmacSignature;

        public VoteMetadataRecord(int voterId, Timestamp votingTimestamp, String hmacSignature) {
            this.voterId = voterId;
            this.votingTimestamp = votingTimestamp;
            this.hmacSignature = hmacSignature;
        }
    }

    public static List<VoteMetadataRecord> getMetadataForVoting(int votingId) throws SQLException {
        List<VoteMetadataRecord> list = new ArrayList<>();
        String sql = "select voter_id, voting_timestamp, hmac_signature from vote_metadata where voting_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, votingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new VoteMetadataRecord(
                            rs.getInt("voter_id"),
                            rs.getTimestamp("voting_timestamp"),
                            rs.getString("hmac_signature")
                    ));
                }
            }
        }
        return list;
    }
}

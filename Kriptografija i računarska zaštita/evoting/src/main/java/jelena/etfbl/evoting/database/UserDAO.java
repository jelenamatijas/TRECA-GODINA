package jelena.etfbl.evoting.database;

import jelena.etfbl.evoting.model.User;
import jelena.etfbl.evoting.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static boolean existsByUsername(String username)throws SQLException{
        String sql = "select count(*) from voting_user where username = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static int registerOrganizer(
            String username,
            String passwordHash,
            String orgName,
            String idNumber,
            String certSerial) throws SQLException {

        String sql = "INSERT INTO voting_user (username, password_hash, user_role, organization_name, id_number, certificate_serial_number) " +
                "VALUES (?, ?, 'ORGANIZATOR', ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, orgName);
            pstmt.setString(4, idNumber);
            pstmt.setString(5, certSerial);

            int affectedRows = pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB GRESKA] Neuspjesan unos organizatora u bazu: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public static int registerVoter(
            String username,
            String passwordHash,
            String firstName,
            String lastName,
            String certSerial) throws SQLException {

        String sql = "INSERT INTO voting_user (username, password_hash, user_role, first_name, last_name, certificate_serial_number) " +
                "VALUES (?, ?, 'GLASAC', ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, certSerial);

            int affectedRows = pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB GRESKA] Neuspjesan unos glasača u bazu: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public static User findByUsername(String username)throws SQLException{
        String sql = "select * from voting_user where username = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setUserRole(UserRole.valueOf(rs.getString("user_role")));
                    user.setOrganizationName(rs.getString("organization_name"));
                    user.setIdNumber(rs.getString("id_number"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setCertificateSerialNumber(rs.getString("certificate_serial_number"));
                    user.setFailedAttempts(rs.getInt("failed_attempts"));
                    user.setRevoked(rs.getBoolean("is_revoked"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    public static void incrementFailedAttempts(int userId)throws  SQLException{
        String sql = "update voting_user set failed_attempts = failed_attempts+1 where user_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public static void markAsRevoked(int userId)throws SQLException{
        String sql = "update voting_user set is_revoked = 1 where user_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public static void resetUserStatus(String username) throws SQLException {
        String sql = "UPDATE voting_user SET is_revoked = 0, failed_attempts = 0 WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    public static List<String> getAllVoterUsernames() throws SQLException {
        List<String> voters = new ArrayList<>();
        String sql = "SELECT username FROM voting_user WHERE user_role = 'GLASAC'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                voters.add(rs.getString("username"));
            }
        }
        return voters;
    }

}

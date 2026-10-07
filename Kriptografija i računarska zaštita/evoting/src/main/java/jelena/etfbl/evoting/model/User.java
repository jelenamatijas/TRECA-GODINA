package jelena.etfbl.evoting.model;


import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private UserRole userRole;
    private String organizationName;
    private String idNumber;
    private String firstName;
    private String lastName;
    private String certificateSerialNumber;
    private int failedAttempts;
    private boolean isRevoked;
    private Timestamp createdAt;

    public User(){}

    public User( int userId,
                 String username,
                 String passwordHash,
                 UserRole userRole,
                 String organizationName,
                 String idNumber,
                 String firstName,
                 String lastName,
                 String certificateSerialNumber,
                 int failedAttempts,
                 boolean isRevoked,
                 Timestamp createdAt){
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.organizationName = organizationName;
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.certificateSerialNumber = certificateSerialNumber;
        this.failedAttempts = failedAttempts;
        this.isRevoked = isRevoked;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCertificateSerialNumber() { return certificateSerialNumber; }
    public void setCertificateSerialNumber(String certificateSerialNumber) { this.certificateSerialNumber = certificateSerialNumber; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public boolean isRevoked() { return isRevoked; }
    public void setRevoked(boolean revoked) { isRevoked = revoked; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

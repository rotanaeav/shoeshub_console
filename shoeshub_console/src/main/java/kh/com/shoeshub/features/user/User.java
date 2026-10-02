package kh.com.shoeshub.features.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private UserRole role;
    private String fullName;
    private String username;
    private String passwordHash;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private boolean active;
    private boolean deleted;
    private Timestamp createdAt;
}

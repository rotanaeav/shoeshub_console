package kh.com.shoeshub.features.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private boolean active;
    private boolean deleted;
    private OffsetDateTime createdAt;
}

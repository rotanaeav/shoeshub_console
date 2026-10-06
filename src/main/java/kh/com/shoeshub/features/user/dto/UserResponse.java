package kh.com.shoeshub.features.user.dto;

import kh.com.shoeshub.features.user.UserRole;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String username,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address,
        UserRole role,
        Timestamp createdAt
) {
}

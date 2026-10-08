package kh.com.shoeshub.features.user.dto;

import kh.com.shoeshub.features.user.UserRole;

import java.time.LocalDate;

public record CreateUserRequest(
        String fullName,
        String username,
        String password,
        String phone,
        LocalDate dob,
        String gender,
        String address,
        UserRole role
) {

}

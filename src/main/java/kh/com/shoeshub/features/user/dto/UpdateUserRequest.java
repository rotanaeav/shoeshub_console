package kh.com.shoeshub.features.user.dto;

import java.time.LocalDate;

public record UpdateUserRequest(
        String fullName,
        String username,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String address
) {

}

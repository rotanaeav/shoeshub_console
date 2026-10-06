package kh.com.shoeshub.features.user.dto;

import java.time.LocalDate;

public record CreateUserRequest(
        String fullName,
        String username,
        String password,
        String phone,
        LocalDate dob,
        String gender,
        String address
) {

}

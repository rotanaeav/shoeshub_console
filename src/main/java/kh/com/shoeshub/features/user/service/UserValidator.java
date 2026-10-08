package kh.com.shoeshub.features.user.service;

import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;

public class UserValidator {

    public void validateCreateUser(CreateUserRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        validateUsername(request.username());
        validatePhoneNumber(request.phone());
        validatePassword(request.password());
        validateGender(request.gender());
    }

    public void validateUpdateUser(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (request.gender() != null && !request.gender().isBlank()) {
            validateGender(request.gender());
        }
    }

    private void validateGender(String gender) {
        if (gender == null || gender.isBlank()) {
            throw new IllegalArgumentException("Gender is required");
        }

        String upper = gender.trim().toUpperCase();
        if (!upper.equals("MALE") && !upper.equals("FEMALE")) {
            throw new IllegalArgumentException(
                    "Gender must be either 'MALE' or 'FEMALE'"
            );
        }
    }

    private void validateUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (username.length() < 3 || username.length() > 30) {
            throw new IllegalArgumentException(
                    "Username must be between 3 and 30 characters"
            );
        }
    }

    private void validatePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (!phoneNumber.matches("^\\+?[0-9]{8,15}$")) {
            throw new IllegalArgumentException(
                    "Invalid phone number format"
            );
        }
    }

    private void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }
    }
}
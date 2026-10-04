package kh.com.shoeshub.features.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
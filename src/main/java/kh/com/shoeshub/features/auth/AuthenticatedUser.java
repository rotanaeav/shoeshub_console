package kh.com.shoeshub.features.auth;

import kh.com.shoeshub.features.user.UserRole;

import java.util.UUID;

public record AuthenticatedUser(
        UUID id,
        String username,
        UserRole role
) {
}
package kh.com.shoeshub.features.auth.service;

import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.auth.dto.LoginRequest;
import kh.com.shoeshub.features.user.User;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.utils.PasswordUtil;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticatedUser login(LoginRequest request) {

        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.password() == null
                || request.password().isBlank()) {

            throw new IllegalArgumentException(
                    "Username and password are required"
            );
        }

        User user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid username or password"
                ));

        if (user.isDeleted()) {
            throw new IllegalArgumentException(
                    "Account has been deleted"
            );
        }
        boolean passwordMatched = PasswordUtil.verifyPassword(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatched) {
            throw new IllegalArgumentException(
                    "Invalid username or password"
            );
        }

        return new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
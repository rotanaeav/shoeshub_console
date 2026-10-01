package kh.com.shoeshub.features.auth;

import kh.com.shoeshub.features.auth.service.UserService;
import kh.com.shoeshub.features.auth.service.UserServiceImpl;

public class UserController {

    private final UserService userService = new UserServiceImpl();
    private final UserUI userUI = new UserUI();

    public void handleRegister() {
        // TODO: Implement register action (Customer self-registration)
    }

    public void handleLogin() {
        // TODO: Implement login action
    }

    public void handleViewProfile() {
        // TODO: Implement view profile action
    }

    public void handleUserManagement() {
        // TODO: Admin-only action: create ADMIN/SELLER users, list users, activate/deactivate
    }
}

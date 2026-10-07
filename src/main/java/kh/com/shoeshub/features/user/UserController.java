package kh.com.shoeshub.features.user;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;
import kh.com.shoeshub.features.user.service.UserService;

import java.util.List;
import java.util.UUID;


public class UserController {

    private final UserService userService;
    private final Security security;

    public UserController(
            UserService userService,
            Security security
    ) {
        this.userService = userService;
        this.security = security;
    }
    public UserResponse getCurrentUserCtrl() {

        AuthenticatedUser currentUser = security.getCurrentUser();

        return userService.findUserById(currentUser.id());
    }
    public UserResponse createUserCtrl(CreateUserRequest request) {
        return userService.saveUser(request);
    }

    public UserResponse findUserByIdCtrl(UUID id) {
        return userService.findUserById(id);
    }

    public UserResponse findUserByUsernameCtrl(String username) {
        return userService.findUserByUsername(username);
    }

    public List<UserResponse> findAllUsersCtrl() {
        return userService.findAllUsers();
    }

    public UserResponse updateUserCtrl(UUID id, UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    public boolean softDeleteCtrl(UUID id) {
        return userService.softDeleteUser(id);
    }

    public UserResponse restoreUserCtrl(UUID id) {
        return userService.restoreUser(id);
    }

    public void permanentDeleteUserCtrl(UUID id) {
        userService.permanentDeleteUser(id);
    }
}

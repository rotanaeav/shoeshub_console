package kh.com.shoeshub.features.auth;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.config.ServiceProvider;
import kh.com.shoeshub.features.auth.dto.LoginRequest;
import kh.com.shoeshub.features.auth.service.AuthService;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.time.LocalDate;

public class AuthUI {
    private static final UserController userController = ServiceProvider.getUserController();
    private static final AuthService authService = ServiceProvider.getAuthService();
    private static final Security security = new Security();

    public void handleRegister() {

        try {
            OutputUtil.printSubHeader("Register");

            String fullName =
                    InputUtil.readRequiredText("Full name");

            String username =
                    InputUtil.readRequiredText(
                            "Username (3-30 characters)"
                    );

            String password =
                    InputUtil.readRequiredText(
                            "Password (at least 8 characters)"
                    );

            String phone =
                    InputUtil.readRequiredText("Phone");

            LocalDate dob = LocalDate.parse(
                    InputUtil.readRequiredText(
                            "Date of birth (YYYY-MM-DD)"
                    )
            );

            String gender =
                    InputUtil.readRequiredText("Gender")
                            .toUpperCase();

            String address =
                    InputUtil.readText("Address (optional)");

            CreateUserRequest request =
                    new CreateUserRequest(
                            fullName,
                            username,
                            password,
                            phone,
                            dob,
                            gender,
                            address,
                            UserRole.CUSTOMER
                    );

            userController.createUserCtrl(request);

            OutputUtil.printSuccess(
                    "Account created. You can login now."
            );

        } catch (Exception e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    public AuthenticatedUser handleLogin() {

        try {

            OutputUtil.printSubHeader("LOGIN");

            String username =
                    InputUtil.readRequiredText("Username");

            String password =
                    InputUtil.readRequiredText("Password");

            LoginRequest request =
                    new LoginRequest(username, password);

            AuthenticatedUser user =
                    authService.login(request);

            security.authenticate(user);

            OutputUtil.printSuccess(
                    "Welcome, " + user.username()
            );

            return user;

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
            return null;
        }
    }
}

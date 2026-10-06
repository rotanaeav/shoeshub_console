package kh.com.shoeshub.features.auth;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.features.auth.dto.LoginRequest;
import kh.com.shoeshub.features.auth.service.AuthService;
import kh.com.shoeshub.features.auth.service.AuthServiceImpl;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;

import java.util.Scanner;

public class AuthUI {


    void main() {

        Scanner sc = new Scanner(System.in);

        // INITIALIZE DEPENDENCIES
        UserRepository userRepository = new UserRepositoryImpl();

        // Adjust this constructor to match your AuthServiceImpl
        AuthService authService = new AuthServiceImpl(userRepository);

        Security security = new Security();

        while (true) {

            System.out.println("\n========== AUTH TEST ==========");
            System.out.println("1. Login");
            System.out.println("2. Check Login Status");
            System.out.println("3. View Current User");
            System.out.println("4. Logout");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine();

            try {
                switch (choice) {

                    // LOGIN
                    case "1" -> {
                        if (security.isAuthenticated()) {
                            System.out.println("You are already logged in.");
                            break;
                        }

                        System.out.println("\n========== LOGIN ==========");

                        System.out.print("Username: ");
                        String username = sc.nextLine();

                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        LoginRequest request = new LoginRequest(
                                username,
                                password
                        );

                        AuthenticatedUser user = authService.login(request);

                        security.authenticate(user);

                        System.out.println("\nLogin successful!");
                        System.out.println("Welcome, " + user.username());
                    }

                    // CHECK LOGIN STATUS
                    case "2" -> {
                        System.out.println("\n========== LOGIN STATUS ==========");

                        if (security.isAuthenticated()) {
                            System.out.println("You are logged in.");
                        } else {
                            System.out.println("You are not logged in.");
                        }
                    }

                    // VIEW CURRENT USER
                    case "3" -> {
                        System.out.println("\n========== CURRENT USER ==========");

                        if (!security.isAuthenticated()) {
                            System.out.println("Please login first.");
                            break;
                        }

                        AuthenticatedUser user = security.getCurrentUser();

                        System.out.println("ID: " + user.id());
                        System.out.println("Username: " + user.username());
                        System.out.println("Role: " + user.role());
                    }

                    // LOGOUT
                    case "4" -> {
                        System.out.println("\n========== LOGOUT ==========");

                        if (!security.isAuthenticated()) {
                            System.out.println("You are not logged in.");
                            break;
                        }

                        security.logout();

                        System.out.println("Logout successful!");
                    }

                    case "0" -> {
                        System.out.println("Exiting...");
                        return;
                    }

                    default -> System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("\nOperation failed: " + e.getMessage());
            }
        }
    }
}

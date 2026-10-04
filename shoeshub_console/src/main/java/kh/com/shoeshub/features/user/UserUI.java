package kh.com.shoeshub.features.user;

import kh.com.shoeshub.config.ServiceProvider;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

public class UserUI {
    void main() {

        Scanner sc = new Scanner(System.in);

        UserController userController = ServiceProvider.getUserController();

        UUID lastUserId = null;

        while (true) {

            System.out.println("\n========== USER TEST MENU ==========");
            System.out.println("1. Create User");
            System.out.println("2. Find User By ID");
            System.out.println("3. Find User By Username");
            System.out.println("4. Find All Users");
            System.out.println("5. Update User");
            System.out.println("6. Soft Delete User");
            System.out.println("7. Restore User");
            System.out.println("8. Permanent Delete User");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            try {
                switch (choice) {

                    case 1 -> {
                        System.out.println("\n========== CREATE USER ==========");

                        System.out.print("Full Name: ");
                        String fullName = sc.nextLine();

                        System.out.print("Username: ");
                        String username = sc.nextLine();

                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        System.out.print("Phone: ");
                        String phone = sc.nextLine();

                        System.out.print("Date of Birth (YYYY-MM-DD): ");
                        LocalDate dob = LocalDate.parse(sc.nextLine());

                        System.out.print("Gender: ");
                        String gender = sc.nextLine();

                        System.out.print("Address: ");
                        String address = sc.nextLine();

                        CreateUserRequest request = new CreateUserRequest(
                                fullName,
                                username,
                                password,
                                phone,
                                dob,
                                gender.toUpperCase(Locale.ROOT),
                                address
                        );

                        UserResponse response = userController.createUserCtrl(request);

                        lastUserId = response.id();

                        System.out.println("\nUser created successfully!");
                        printUser(response);
                        System.out.println("Saved ID for testing: " + lastUserId);
                    }

                    // FIND BY ID
                    case 2 -> {
                        System.out.println("\n========== FIND USER BY ID ==========");

                        UUID id = readUserId(sc, lastUserId);

                        UserResponse response = userController.findUserByIdCtrl(id);

                        printUser(response);
                    }

                    // FIND BY USERNAME
                    case 3 -> {
                        System.out.println("\n========== FIND USER BY USERNAME ==========");

                        System.out.print("Enter username: ");
                        String username = sc.nextLine();

                        UserResponse response =
                                userController.findUserByUsernameCtrl(username);

                        printUser(response);
                    }

                    // FIND ALL
                    case 4 -> {
                        System.out.println("\n========== FIND ALL USERS ==========");

                        List<UserResponse> users = userController.findAllUsersCtrl();

                        if (users.isEmpty()) {
                            System.out.println("No users found.");
                        } else {
                            users.forEach(UserUI::printUser);
                        }
                    }

                    // UPDATE USER
                    case 5 -> {
                        System.out.println("\n========== UPDATE USER ==========");

                        UUID id = readUserId(sc, lastUserId);

                        System.out.println("Leave a field empty to keep its current value.");

                        System.out.print("Full Name: ");
                        String fullName = emptyToNull(sc.nextLine());

                        System.out.print("Username: ");
                        String username = emptyToNull(sc.nextLine());

                        System.out.print("Phone: ");
                        String phone = emptyToNull(sc.nextLine());

                        System.out.print("Date of Birth (YYYY-MM-DD): ");
                        String dobInput = sc.nextLine();
                        LocalDate dateOfBirth = dobInput.isBlank()
                                ? null
                                : LocalDate.parse(dobInput);

                        System.out.print("Gender: ");
                        String gender = emptyToNull(sc.nextLine());

                        System.out.print("Address: ");
                        String address = emptyToNull(sc.nextLine());

                        UpdateUserRequest request = new UpdateUserRequest(
                                fullName,
                                username,
                                phone,
                                dateOfBirth,
                                gender,
                                address
                        );

                        UserResponse response =
                                userController.updateUserCtrl(id, request);

                        System.out.println("\nUser updated successfully!");
                        printUser(response);
                    }

                    // SOFT DELETE
                    case 6 -> {
                        System.out.println("\n========== SOFT DELETE USER ==========");

                        UUID id = readUserId(sc, lastUserId);

                        System.out.print("Are you sure? (y/n): ");
                        String confirm = sc.nextLine();

                        if (confirm.equalsIgnoreCase("y")) {
                            boolean deleted = userController.softDeleteCtrl(id);

                            System.out.println(
                                    deleted
                                            ? "User soft-deleted successfully."
                                            : "User was not deleted."
                            );
                        } else {
                            System.out.println("Cancelled.");
                        }
                    }

                    // RESTORE
                    case 7 -> {
                        System.out.println("\n========== RESTORE USER ==========");

                        UUID id = readUserId(sc, lastUserId);

                        UserResponse response = userController.restoreUserCtrl(id);

                        System.out.println("\nUser restored successfully!");
                        printUser(response);
                    }

                    // PERMANENT DELETE
                    case 8 -> {
                        System.out.println("\n========== PERMANENT DELETE USER ==========");

                        UUID id = readUserId(sc, lastUserId);

                        System.out.print("This cannot be undone. Continue? (y/n): ");
                        String confirm = sc.nextLine();

                        if (confirm.equalsIgnoreCase("y")) {
                            userController.permanentDeleteUserCtrl(id);

                            System.out.println("User permanently deleted.");

                            if (id.equals(lastUserId)) {
                                lastUserId = null;
                            }
                        } else {
                            System.out.println("Cancelled.");
                        }
                    }

                    case 0 -> {
                        System.out.println("Exiting...");
                        return;
                    }

                    default -> System.out.println("Invalid choice. Try again.");
                }

            } catch (Exception e) {
                System.out.println("\nOperation failed: " + e.getMessage());
            }
        }
    }

    static void printUser(UserResponse user) {
        System.out.println("------------------------------------");
        System.out.println("ID: " + user.id());
        System.out.println("Full Name: " + user.fullName());
        System.out.println("Username: " + user.username());
        System.out.println("Phone: " + user.phone());
        System.out.println("Date of Birth: " + user.dateOfBirth());
        System.out.println("Gender: " + user.gender());
        System.out.println("Address: " + user.address());
        System.out.println("Role: " + user.role());
        System.out.println("Created At: " + user.createdAt());
        System.out.println("------------------------------------");
    }

    // READ USER ID
    static UUID readUserId(Scanner sc, UUID lastUserId) {

        if (lastUserId != null) {
            System.out.println("Last created user ID: " + lastUserId);
        }

        System.out.print("Enter User ID (press Enter to use last ID): ");
        String input = sc.nextLine();

        if (input.isBlank()) {
            if (lastUserId == null) {
                throw new IllegalArgumentException("No saved user ID. Please enter an ID.");
            }

            return lastUserId;
        }

        return UUID.fromString(input);
    }

    // CONVERT EMPTY STRING TO NULL
    static String emptyToNull(String value) {
        return value.isBlank() ? null : value;
    }


}


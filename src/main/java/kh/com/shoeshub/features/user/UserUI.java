package kh.com.shoeshub.features.user;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UserUI {
    private  final Security security;
    private  final UserController userController;

    public UserUI(Security security, UserController userController) {
        this.security = security;
        this.userController = userController;
    }

    public  void handleUserManagement() {

        while (true) {
            OutputUtil.printHeader("USER MANAGEMENT");
            OutputUtil.println("""
                [1] Create Admin
                [2] Create Seller
                [3] Find User By ID
                [4] Find User By Username
                [5] Find All Users
                [6] Update User
                [7] Soft Delete User
                [8] Restore User
                [9] Permanent Delete User
                [0] Back
                """);

            int choice = InputUtil.readInt("Choose menu", 0, 9);

            switch (choice) {

                case 1 -> handleCreateAdmin();

                case 2 -> handleCreateSeller();

                case 3 -> handleFindUserById();

                case 4 -> handleFindUserByUsername();

                case 5 -> handleFindAllUsers();

                case 6 -> handleUpdateUser();

                case 7 -> handleSoftDeleteUser();

                case 8 -> handleRestoreUser();

                case 9 -> handlePermanentDeleteUser();

                case 0 -> {
                    return;
                }

                default ->
                        OutputUtil.printError("Invalid menu choice.");
            }
        }
    }

    public  void printUser(UserResponse user) {

        OutputUtil.println("----------------------------------------");
        OutputUtil.println("ID: " + user.id());
        OutputUtil.println("Full Name: " + user.fullName());
        OutputUtil.println("Username: " + user.username());
        OutputUtil.println("Phone: " + user.phone());
        OutputUtil.println("Date of Birth: " + user.dateOfBirth());
        OutputUtil.println("Gender: " + user.gender());
        OutputUtil.println("Address: " + user.address());
        OutputUtil.println("Role: " + user.role());
        OutputUtil.println("Created At: " + user.createdAt());
        OutputUtil.println("----------------------------------------");
    }

    public  void handleViewProfile() {

        try {

            OutputUtil.printSubHeader("MY PROFILE");

            UserResponse user =
                    userController.getCurrentUserCtrl();

            printUser(user);

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleCreateAdmin() {

        try {

            OutputUtil.printSubHeader("CREATE ADMIN");

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
                            UserRole.ADMIN
                    );

            userController.createUserCtrl(request);

            OutputUtil.printSuccess(
                    "Admin account created successfully."
            );

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }
    public  void handleCreateSeller() {

        try {

            OutputUtil.printSubHeader("CREATE SELLER");

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
                            UserRole.SELLER
                    );

            userController.createUserCtrl(request);

            OutputUtil.printSuccess(
                    "Seller account created successfully."
            );

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleFindUserById() {

        try {

            OutputUtil.printSubHeader("FIND USER BY ID");

            String input =
                    InputUtil.readRequiredText("User ID");

            UUID id = UUID.fromString(input);

            UserResponse user =
                    userController.findUserByIdCtrl(id);

            printUser(user);

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleFindUserByUsername() {

        try {

            OutputUtil.printSubHeader("FIND USER BY USERNAME");

            String username =
                    InputUtil.readRequiredText("Username");

            UserResponse user =
                    userController.findUserByUsernameCtrl(username);

            printUser(user);

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleFindAllUsers() {

        try {

            OutputUtil.printSubHeader("ALL USERS");

            List<UserResponse> users =
                    userController.findAllUsersCtrl();

            if (users.isEmpty()) {

                OutputUtil.printInfo("No users found.");
                return;
            }

            for (UserResponse user : users) {
                printUser(user);
            }

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    private static String emptyToNull(String value) {
        return value.isBlank() ? null : value;
    }

    public  void handleUpdateUser() {

        try {

            OutputUtil.printSubHeader("UPDATE USER");

            String input =
                    InputUtil.readRequiredText("User ID");

            UUID id = UUID.fromString(input);

            OutputUtil.println(
                    "Leave a field empty to keep the current value."
            );

            String fullName =
                    InputUtil.readText("Full name");

            String username =
                    InputUtil.readText("Username");

            String phone =
                    InputUtil.readText("Phone");

            String dobInput =
                    InputUtil.readText(
                            "Date of birth (YYYY-MM-DD)"
                    );

            LocalDate dateOfBirth = dobInput.isBlank()
                            ? null
                            : LocalDate.parse(dobInput);

            String gender =
                    InputUtil.readText("Gender");

            String address =
                    InputUtil.readText("Address");

            UpdateUserRequest request =
                    new UpdateUserRequest(
                            emptyToNull(fullName),
                            emptyToNull(username),
                            emptyToNull(phone),
                            dateOfBirth,
                            emptyToNull(gender),
                            emptyToNull(address)
                    );

            UserResponse response =
                    userController.updateUserCtrl(id, request);

            OutputUtil.printSuccess(
                    "User updated successfully."
            );

            printUser(response);

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleSoftDeleteUser() {

        try {

            OutputUtil.printSubHeader("SOFT DELETE USER");

            String input =
                    InputUtil.readRequiredText("User ID");

            UUID id = UUID.fromString(input);

            String confirm =
                    InputUtil.readRequiredText(
                            "Are you sure you want to delete this user? (y/n)"
                    );

            if (!confirm.equalsIgnoreCase("y")) {
                OutputUtil.printInfo("Cancelled.");
                return;
            }

            boolean deleted =
                    userController.softDeleteCtrl(id);

            if (deleted) {
                OutputUtil.printSuccess(
                        "User soft-deleted successfully."
                );
            } else {
                OutputUtil.printError(
                        "User was not deleted."
                );
            }

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handleRestoreUser() {

        try {

            OutputUtil.printSubHeader("RESTORE USER");

            String input =
                    InputUtil.readRequiredText("User ID");

            UUID id = UUID.fromString(input);

            UserResponse response =
                    userController.restoreUserCtrl(id);

            OutputUtil.printSuccess(
                    "User restored successfully."
            );

            printUser(response);

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public  void handlePermanentDeleteUser() {

        try {

            OutputUtil.printSubHeader("PERMANENT DELETE USER");

            String input =
                    InputUtil.readRequiredText("User ID");

            UUID id = UUID.fromString(input);

            String confirm =
                    InputUtil.readRequiredText(
                            "This cannot be undone. Continue? (y/n)"
                    );

            if (!confirm.equalsIgnoreCase("y")) {
                OutputUtil.printInfo("Cancelled.");
                return;
            }

            userController.permanentDeleteUserCtrl(id);

            OutputUtil.printSuccess(
                    "User permanently deleted."
            );

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }
}

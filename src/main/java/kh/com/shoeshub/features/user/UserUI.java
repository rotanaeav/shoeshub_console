package kh.com.shoeshub.features.user;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.user.dto.UpdateUserRequest;
import kh.com.shoeshub.features.user.dto.UserResponse;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserUI {
    private final Security security;
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

    public void displayUserDetail(UserResponse user) {
        if (user == null) {
            OutputUtil.printInfo("User not found.");
            return;
        }

        OutputUtil.printSubHeader("USER DETAILS");

        Table table = TableUtil.createTable(2, "FIELD", "VALUE");

        table.addCell("ID");
        table.addCell(user.id() != null ? user.id().toString() : "-");

        table.addCell("FULL NAME");
        table.addCell(user.fullName() != null ? user.fullName() : "-");

        table.addCell("USERNAME");
        table.addCell(user.username() != null ? user.username() : "-");

        table.addCell("PHONE");
        table.addCell(user.phone() != null ? user.phone() : "-");

        table.addCell("DATE OF BIRTH");
        table.addCell(user.dateOfBirth() != null ? user.dateOfBirth().toString() : "-");

        table.addCell("GENDER");
        table.addCell(user.gender() != null ? user.gender() : "-");

        table.addCell("ADDRESS");
        table.addCell(user.address() != null ? user.address() : "-");

        table.addCell("ROLE");
        table.addCell(user.role() != null ? user.role().toString() : "-");

        table.addCell("CREATED AT");
        table.addCell(user.createdAt() != null ? user.createdAt().toString() : "-");

        TableUtil.render(table);
    }

    private void renderUserTable(List<UserResponse> users, String title, boolean numbered) {

        if (users == null || users.isEmpty()) {
            OutputUtil.printInfo("No users found.");
            return;
        }
        OutputUtil.printSubHeader(title);

        List<String> headers = new ArrayList<>();

        if (numbered) {
            headers.add("#");
        }
        headers.addAll(List.of(
                "USERNAME",
                "FULL NAME",
                "PHONE",
                "GENDER",
                "ROLE",
                "STATUS"
        ));
        Table table = TableUtil.createTable(
                headers.size(),
                headers.toArray(new String[0])
        );
        int no = 1;

        for (UserResponse user : users) {

            if (numbered) {
                table.addCell(String.valueOf(no++));
            }
            table.addCell(user.username() != null ? user.username() : "-");
            table.addCell(user.fullName() != null ? user.fullName() : "-");
            table.addCell(user.phone() != null ? user.phone() : "-");
            table.addCell(user.gender() != null ? user.gender() : "-");
            table.addCell(user.role() != null ? user.role().toString() : "-");
            table.addCell("ACTIVE");
        }
        TableUtil.render(table);
    }

    public void displayUsers(List<UserResponse> users) {
        renderUserTable(users, "USER LIST", false);
    }

    public  void handleViewProfile() {

        try {

            OutputUtil.printSubHeader("MY PROFILE");

            UserResponse user =
                    userController.getCurrentUserCtrl();

            displayUserDetail(user);

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

            LocalDate dob;
            while (true) {
                try {
                    dob = LocalDate.parse(InputUtil.readRequiredText("Date of birth (YYYY-MM-DD)"));
                    break;
                } catch (Exception e) {
                    OutputUtil.printError("Invalid date format. Example: 2000-01-15");
                }
            }

            String gender;
            while (true) {
                gender = InputUtil.readRequiredText("Gender (MALE / FEMALE)").trim().toUpperCase();
                if (gender.equals("MALE") || gender.equals("FEMALE")) {
                    break;
                }
                OutputUtil.printError("Gender must be either MALE or FEMALE.");
            }

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

            LocalDate dob;
            while (true) {
                try {
                    dob = LocalDate.parse(InputUtil.readRequiredText("Date of birth (YYYY-MM-DD)"));
                    break;
                } catch (Exception e) {
                    OutputUtil.printError("Invalid date format. Example: 2000-01-15");
                }
            }

            String gender;
            while (true) {
                gender = InputUtil.readRequiredText("Gender (MALE / FEMALE)").trim().toUpperCase();
                if (gender.equals("MALE") || gender.equals("FEMALE")) {
                    break;
                }
                OutputUtil.printError("Gender must be either MALE or FEMALE.");
            }

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

    private UUID resolveUserId(String prompt) {
        String input = InputUtil.readRequiredText(prompt).trim();
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            UserResponse user = userController.findUserByUsernameCtrl(input);
            if (user == null || user.id() == null) {
                throw new IllegalArgumentException("User not found with username: " + input);
            }
            return user.id();
        }
    }

    public void handleFindUserById() {

        try {

            OutputUtil.printSubHeader("FIND USER BY USERNAME OR ID");

            UUID id = resolveUserId("Username or User ID");

            UserResponse user =
                    userController.findUserByIdCtrl(id);

            displayUserDetail(user);

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

            displayUserDetail(user);

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public void handleFindAllUsers() {
        try {
            List<UserResponse> users = userController.findAllUsersCtrl();

            displayUsers(users);

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

            UUID id = resolveUserId("Username or User ID");

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

            LocalDate dateOfBirth = null;
            if (!dobInput.isBlank()) {
                while (true) {
                    try {
                        dateOfBirth = LocalDate.parse(dobInput.trim());
                        break;
                    } catch (Exception e) {
                        OutputUtil.printError("Invalid date format. Example: 2000-01-15 (or leave empty to keep)");
                        dobInput = InputUtil.readText("Date of birth (YYYY-MM-DD)");
                        if (dobInput.isBlank()) {
                            dateOfBirth = null;
                            break;
                        }
                    }
                }
            }

            String genderInput =
                    InputUtil.readText("Gender (MALE / FEMALE)");

            String gender = null;
            if (!genderInput.isBlank()) {
                while (true) {
                    String g = genderInput.trim().toUpperCase();
                    if (g.equals("MALE") || g.equals("FEMALE")) {
                        gender = g;
                        break;
                    }
                    OutputUtil.printError("Gender must be either MALE or FEMALE (or leave empty to keep).");
                    genderInput = InputUtil.readText("Gender (MALE / FEMALE)");
                    if (genderInput.isBlank()) {
                        break;
                    }
                }
            }

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

            displayUserDetail(response);

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public void handleSoftDeleteUser() {

        try {

            OutputUtil.printSubHeader("SOFT DELETE USER");

            UUID id = resolveUserId("Username or User ID");

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

    public void handleRestoreUser() {

        try {

            OutputUtil.printSubHeader("RESTORE USER");

            UUID id = resolveUserId("Username or User ID");

            UserResponse response =
                    userController.restoreUserCtrl(id);

            OutputUtil.printSuccess(
                    "User restored successfully."
            );

            displayUserDetail(response);

        } catch (IllegalArgumentException e) {

            OutputUtil.printError(e.getMessage());

        } catch (Exception e) {

            OutputUtil.printError(e.getMessage());
        }
    }

    public void handlePermanentDeleteUser() {

        try {

            OutputUtil.printSubHeader("PERMANENT DELETE USER");

            UUID id = resolveUserId("Username or User ID");

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

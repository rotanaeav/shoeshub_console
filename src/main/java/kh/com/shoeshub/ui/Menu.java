package kh.com.shoeshub.ui;


import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.config.ServiceProvider;
import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.category.CategoryController;
import kh.com.shoeshub.features.order.OrderController;
import kh.com.shoeshub.features.payment.PaymentController;
import kh.com.shoeshub.features.product.ProductController;
import kh.com.shoeshub.features.report.ReportController;
import kh.com.shoeshub.features.review.ReviewController;
import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.features.user.dto.CreateUserRequest;
import kh.com.shoeshub.features.wishlist.WishlistController;
import kh.com.shoeshub.features.user.mapper.UserMapper;
import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;
import kh.com.shoeshub.features.user.service.UserService;
import kh.com.shoeshub.features.user.service.UserServiceImpl;
import kh.com.shoeshub.features.user.service.UserValidator;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.features.auth.dto.LoginRequest;
import kh.com.shoeshub.features.auth.service.AuthService;
import kh.com.shoeshub.features.auth.service.AuthServiceImpl;

import java.util.UUID;

//import kh.com.shoeshub.authorize.AuthorizationService;
//import kh.com.shoeshub.authorize.Security;

public class Menu {

    // Controllers

    private static final Security security = new Security();
    private static final AuthorizationService authorizationService = new AuthorizationService(security);

    private static  final UserRepository userRepository = new UserRepositoryImpl();
    private static final AuthService authService = new AuthServiceImpl(userRepository);
    private static final UserController userController = ServiceProvider.getUserController();

    private static final ProductController productController = new ProductController(authorizationService);
    private static final CategoryController categoryController = new CategoryController(authorizationService);

    private static final CartController cartController = ServiceProvider.getCartController();
    private static final WishlistController wishlistController = new WishlistController();
    private static final OrderController orderController = new OrderController();
    private static final PaymentController paymentController = new PaymentController();
    private static final ReviewController reviewController = new ReviewController();
    private static final ReportController reportController = new ReportController();

    /**
     * 1. Public / Guest Menu (Main Switch)
     */
    public static void displayMainMenu() {
        OutputUtil.printBanner();

        while (true) {
            OutputUtil.printHeader("MAIN MENU");
            OutputUtil.println("""
                            [1] BROWSE PRODUCTS
                            [2] SEARCH PRODUCTS
                            [3] LOGIN
                            [4] REGISTER
                            [0] EXIT APPLICATION
                            """);

            int choice = InputUtil.readInt("Choose menu", 0, 4);

            switch (choice) {
                case 1 -> productController.handleBrowseProducts();
                case 2 -> productController.handleSearchProducts();
                case 3 -> handleLogin();
                case 4 -> handleRegister();
                case 0 -> {
                    security.logout();
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }

    private static void handleLogin() {
        try {
            String username = InputUtil.readRequiredText("Username");
            String password = InputUtil.readRequiredText("Password");

            AuthenticatedUser user = authService.login(new LoginRequest(username, password));
            security.authenticate(user);
            OutputUtil.printSuccess("Welcome, " + user.username());

            switch (user.role()) {
                case ADMIN -> displayAdminMenu();
                case SELLER -> displaySellerMenu();
                case CUSTOMER -> displayCustomerMenu();
            }
        } catch (Exception e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    private static void handleRegister() {
        try {
            OutputUtil.printSubHeader("Register");
            String fullName = InputUtil.readRequiredText("Full name");
            String username = InputUtil.readRequiredText("Username (3-30 characters)");
            String password = InputUtil.readRequiredText("Password (at least 8 characters)");
            String phone = InputUtil.readRequiredText("Phone");
            java.time.LocalDate dob = java.time.LocalDate.parse(
                    InputUtil.readRequiredText("Date of birth (YYYY-MM-DD)"));
            String gender = InputUtil.readRequiredText("Gender").toUpperCase();
            String address = InputUtil.readText("Address (optional)");

            userController.createUserCtrl(new CreateUserRequest(
                    fullName, username, password, phone, dob, gender, address));
            OutputUtil.printSuccess("Account created. You can login now.");
        } catch (Exception e) {
            OutputUtil.printError(e.getMessage());
        }
    }

    public static void displayCustomerMenu() {
        while (true) {
            OutputUtil.printHeader("CUSTOMER DASHBOARD");
            OutputUtil.println(" [1] Browse Shoes Catalog");
            OutputUtil.println(" [2] My Shopping Cart & Checkout");
            OutputUtil.println(" [3] My Wishlist");
            OutputUtil.println(" [4] My Orders & Order History");
            OutputUtil.println(" [5] Payment Simulation & History");
            OutputUtil.println(" [6] Product Reviews & Rating");
            OutputUtil.println(" [7] My Profile");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 7);

            switch (choice) {
//                case 1 -> productController.handleListProducts();
                case 1 -> productController.handleBrowseProducts();
                case 2 -> cartController.handleViewCart();
                case 3 -> wishlistController.handleViewWishlist();
                case 4 -> orderController.handleViewOrderHistory();
                case 5 -> paymentController.handleViewTransactionHistory();
                case 6 -> reviewController.handleAddReview();
//                case 7 -> userController.handleViewProfile();
                case 0 -> {
                    security.logout();
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }

    public static void displayAdminMenu() {
        while (true) {
            OutputUtil.printHeader("ADMIN DASHBOARD");
            OutputUtil.println(" [1] User Management (Create Admin/Seller, Manage Accounts)");
            OutputUtil.println(" [2] Product Management (CRUD & Stock)");
            OutputUtil.println(" [3] Category Management");
            OutputUtil.println(" [4] Order Management & Status Updates");
            OutputUtil.println(" [5] Sales Reports & Revenue Analytics");
            OutputUtil.println(" [6] My Profile");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 6);

            switch (choice) {
//                case 1 -> userController.handleUserManagement(); // Admin-only privilege!
//                case 2 -> productController.handleCreateProduct();
//                case 3 -> categoryController.handleListCategories();
                case 2 -> productController.showMenu();
                case 3 -> categoryController.showMenu();
                case 4 -> orderController.handleUpdateOrderStatus();
                case 5 -> reportController.handleViewRevenue();
//                case 6 -> userController.handleViewProfile();
                case 0 -> {
                    security.logout();
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }

    public static void displaySellerMenu() {
        while (true) {
            OutputUtil.printHeader("SELLER DASHBOARD");
            OutputUtil.println(" [1] Product Management (CRUD & Stock)");
            OutputUtil.println(" [2] Category Management");
            OutputUtil.println(" [3] Order Management & Status Updates");
            OutputUtil.println(" [4] Sales Reports & Revenue Analytics");
            OutputUtil.println(" [5] My Profile");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 5);

            switch (choice) {
//                case 1 -> productController.handleCreateProduct();
//                case 2 -> categoryController.handleListCategories();
                case 1 -> productController.showMenu();
                case 2 -> categoryController.showMenu();
                case 3 -> orderController.handleUpdateOrderStatus();
                case 4 -> reportController.handleViewRevenue();
//                case 5 -> userController.handleViewProfile();
                case 0 -> {
                    security.logout();
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }
    public static void loginForTest(UserRole role) {
        security.authenticate(new AuthenticatedUser(UUID.randomUUID(), "test-" + role, role));
    }
}

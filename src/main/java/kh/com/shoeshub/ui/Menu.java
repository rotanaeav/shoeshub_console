package kh.com.shoeshub.ui;

import kh.com.shoeshub.authorize.Security;
import kh.com.shoeshub.config.ServiceProvider;
import kh.com.shoeshub.features.auth.AuthUI;
import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.auth.service.AuthService;
import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.cart.CartUI;
import kh.com.shoeshub.features.category.CategoryController;
import kh.com.shoeshub.features.order.OrderController;
import kh.com.shoeshub.features.payment.PaymentController;
import kh.com.shoeshub.features.product.ProductController;
import kh.com.shoeshub.features.report.ReportController;
import kh.com.shoeshub.features.review.ReviewController;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.user.UserUI;
import kh.com.shoeshub.features.wishlist.WishlistController;
import kh.com.shoeshub.features.wishlist.WishlistUI;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

public class Menu {

    // Controllers
    private static final Security security = new Security();
    private static final AuthService authService = ServiceProvider.getAuthService();
    private static final UserController userController = ServiceProvider.getUserController(security);
    private static final ProductController productController = new ProductController(ServiceProvider.getAuthorizationService(security));
    private static final CategoryController categoryController = new CategoryController(ServiceProvider.getAuthorizationService(security));
    private static final CartController cartController = ServiceProvider.getCartController(security);
    private static final WishlistController wishlistController = ServiceProvider.getWishlistController(ServiceProvider.getAuthorizationService(security));
    private static final OrderController orderController = new OrderController();
    private static final PaymentController paymentController = new PaymentController();
    private static final ReviewController reviewController = new ReviewController();
    private static final ReportController reportController = new ReportController();

    // UI
    private static final AuthUI authUI = new AuthUI(security, authService, userController);
    private static final UserUI userUI = new UserUI(security, userController);
    private static final CartUI cartUI = new CartUI(cartController);
    private static final WishlistUI wishlistUI = new WishlistUI(wishlistController);

    /**
     * 1. Public / Guest Menu (Main Switch)
     */
    public static void displayMainMenu() {
        OutputUtil.printBanner();

        while (true) {
            OutputUtil.printHeader("MAIN MENU");
            OutputUtil.println(" [1] Browse Products");
            OutputUtil.println(" [2] Search Products");
            OutputUtil.println(" [3] Login");
            OutputUtil.println(" [4] Register");
            OutputUtil.println(" [0] Exit Application");

            int choice = InputUtil.readInt("Choose menu", 0, 4);

            switch (choice) {
                case 1 -> productController.handleListProducts();
                case 2 -> productController.handleSearchProducts();
                case 3 -> handleLogin();
                case 4 -> authUI.handleRegister();
                case 0 -> {
                    OutputUtil.printSuccess("Thank you. Goodbye!");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }

    private static void handleLogin() {
        AuthenticatedUser user = authUI.handleLogin();

        if (user == null) {
            return;
        }

        switch (user.role()) {
            case ADMIN -> displayAdminMenu();
            case SELLER -> displaySellerMenu();
            case CUSTOMER -> displayCustomerMenu();
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
                case 1 -> productController.handleListProducts();
                case 2 -> cartUI.showCartMenu(userController.getCurrentUserCtrl().id());
                case 3 -> wishlistUI.showWishlistMenu();
                case 4 -> orderController.handleViewOrderHistory();
                case 5 -> paymentController.handleViewTransactionHistory();
                case 6 -> reviewController.handleAddReview();
                case 7 -> userUI.handleViewProfile();
                case 0 -> {
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
                case 1 -> userUI.handleUserManagement(); // Admin-only privilege!
                case 2 -> productController.handleCreateProduct();
                case 3 -> categoryController.handleListCategories();
                case 4 -> orderController.handleUpdateOrderStatus();
                case 5 -> reportController.handleViewRevenue();
                case 6 -> userUI.handleViewProfile();
                case 0 -> {
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
                case 1 -> productController.handleCreateProduct();
                case 2 -> categoryController.handleListCategories();
                case 3 -> orderController.handleUpdateOrderStatus();
                case 4 -> reportController.handleViewRevenue();
                case 5 -> userUI.handleViewProfile();
                case 0 -> {
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }
}

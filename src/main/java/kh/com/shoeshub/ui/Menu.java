package kh.com.shoeshub.ui;

import kh.com.shoeshub.authorize.AuthorizationService;
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
    private static final AuthorizationService authorizationService = ServiceProvider.getAuthorizationService(security);
    private static final AuthService authService = ServiceProvider.getAuthService();
    private static final UserController userController = ServiceProvider.getUserController(security);
    private static final CategoryController categoryController = new CategoryController(authorizationService);
    private static final CartController cartController = ServiceProvider.getCartController(security);
    private static final WishlistController wishlistController = ServiceProvider.getWishlistController(authorizationService);
    private static final ProductController productController = ServiceProvider.getProductController(
            authorizationService,
            ServiceProvider.getCartService(security),
            ServiceProvider.getWishlistService(authorizationService)
    );
    private static final kh.com.shoeshub.features.order.service.OrderService orderService = ServiceProvider.getOrderService(authorizationService);
    private static final PaymentController paymentController = ServiceProvider.getPaymentController(security, orderService);
    private static final OrderController orderController = ServiceProvider.getOrderController(authorizationService, paymentController);
    private static final ReviewController reviewController = ServiceProvider.getReviewController(authorizationService);
    private static final ReportController reportController = ServiceProvider.getReportController(authorizationService);

    // UI
    private static final AuthUI authUI = new AuthUI(security, authService, userController);
    private static final UserUI userUI = new UserUI(security, userController);
    private static final CartUI cartUI = new CartUI(cartController, orderController, paymentController);
    private static final WishlistUI wishlistUI = new WishlistUI(wishlistController);

    /**
     * 1. Public / Guest Menu (Main Switch)
     */
    public static void displayMainMenu() {
        OutputUtil.printBanner();

        while (true) {
            OutputUtil.printHeader("MAIN MENU");
            OutputUtil.println(" [1] View Products");
            OutputUtil.println(" [2] Search Products");
            OutputUtil.println(" [3] Login");
            OutputUtil.println(" [4] Register");
            OutputUtil.println(" [5] Product Reviews & Ratings");
            OutputUtil.println(" [0] Exit Application");

            int choice = InputUtil.readInt("Choose menu", 0, 5);

            switch (choice) {
                case 1 -> productController.handleBrowseProducts();
                case 2 -> productController.handleSearchProducts();
                case 3 -> handleLogin();
                case 4 -> authUI.handleRegister();
                case 5 -> reviewController.handleViewProductReviews();
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
            OutputUtil.println(" [1] View Products");
            OutputUtil.println(" [2] Shopping Cart");
            OutputUtil.println(" [3] Wishlist");
            OutputUtil.println(" [4] My Orders");
            OutputUtil.println(" [5] Payment History");
            OutputUtil.println(" [6] Product Reviews");
            OutputUtil.println(" [7] My Profile");
            OutputUtil.println(" [8] Search Products");
            OutputUtil.println(" [9] Filter by Category");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 9);

            switch (choice) {
                case 1 -> productController.handleBrowseProducts(userController.getCurrentUserCtrl().id());
                case 2 -> cartUI.showCartMenu(userController.getCurrentUserCtrl().id());
                case 3 -> wishlistUI.showWishlistMenu();
                case 4 -> orderController.handleViewOrderHistory();
                case 5 -> paymentController.handleViewTransactionHistory();
                case 6 -> reviewController.showMenu();
                case 7 -> userUI.handleViewProfile();
                case 8 -> productController.handleSearchProducts();
                case 9 -> productController.handleFilterByCategory();
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
            OutputUtil.println(" [1] User Management");
            OutputUtil.println(" [2] Product Management");
            OutputUtil.println(" [3] Category Management");
            OutputUtil.println(" [4] Order Management");
            OutputUtil.println(" [5] Payment Management");
            OutputUtil.println(" [6] Reports & Analytics");
            OutputUtil.println(" [7] My Profile");
            OutputUtil.println(" [8] Review Moderation");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 8);

            switch (choice) {
                case 1 -> userUI.handleUserManagement(); // Admin-only privilege!
                case 2 -> productController.showMenu();
                case 3 -> categoryController.showMenu();
                case 4 -> orderController.handleOrderManagement();
                case 5 -> paymentController.handleAdminPaymentMenu();
                case 6 -> reportController.showMenu();
                case 7 -> userUI.handleViewProfile();
                case 8 -> reviewController.showAdminMenu();
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
            OutputUtil.println(" [1] Product Management");
            OutputUtil.println(" [2] Category Management");
            OutputUtil.println(" [3] Order Management");
            OutputUtil.println(" [4] Payment Management");
            OutputUtil.println(" [5] View Product Reviews");
            OutputUtil.println(" [6] My Profile");
            OutputUtil.println(" [0] Logout");

            int choice = InputUtil.readInt("Choose menu", 0, 6);

            switch (choice) {
                case 1 -> productController.showMenu();
                case 2 -> categoryController.showMenu();
                case 3 -> orderController.handleOrderManagement();
                case 4 -> paymentController.handleAdminPaymentMenu();
                case 5 -> reviewController.handleViewProductReviews();
                case 6 -> userUI.handleViewProfile();
                case 0 -> {
                    security.logout();
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }
}

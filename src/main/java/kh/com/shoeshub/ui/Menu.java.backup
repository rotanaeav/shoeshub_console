package kh.com.shoeshub.ui;

import kh.com.shoeshub.config.ServiceProvider;
import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.category.CategoryController;
import kh.com.shoeshub.features.order.OrderController;
import kh.com.shoeshub.features.payment.PaymentController;
import kh.com.shoeshub.features.product.ProductController;
import kh.com.shoeshub.features.report.ReportController;
import kh.com.shoeshub.features.review.ReviewController;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.wishlist.WishlistController;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

public class Menu {

    // Controllers
    private static final UserController userController = ServiceProvider.getUserController();
    private static final ProductController productController = new ProductController();
    private static final CategoryController categoryController = new CategoryController();
    private static final CartController cartController = new CartController();
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
            OutputUtil.println(" [1] Browse Products");
            OutputUtil.println(" [2] Search Products");
            OutputUtil.println(" [3] Login");
            OutputUtil.println(" [4] Register");
            OutputUtil.println(" [0] Exit Application");

            int choice = InputUtil.readInt("Choose menu", 0, 4);

            switch (choice) {
                case 1 -> productController.handleListProducts();
                case 2 -> productController.handleSearchProducts();
                case 3 -> userController.handleLogin();
                case 4 -> userController.handleRegister();
                case 0 -> {
                    OutputUtil.printSuccess("Thank you. Goodbye!");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
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
                case 2 -> cartController.handleViewCart();
                case 3 -> wishlistController.handleViewWishlist();
                case 4 -> orderController.handleViewOrderHistory();
                case 5 -> paymentController.handleViewTransactionHistory();
                case 6 -> reviewController.handleAddReview();
                case 7 -> userController.handleViewProfile();
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
                case 1 -> userController.handleUserManagement(); // Admin-only privilege!
                case 2 -> productController.handleCreateProduct();
                case 3 -> categoryController.handleListCategories();
                case 4 -> orderController.handleUpdateOrderStatus();
                case 5 -> reportController.handleViewRevenue();
                case 6 -> userController.handleViewProfile();
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
                case 5 -> userController.handleViewProfile();
                case 0 -> {
                    OutputUtil.printInfo("Logged out successfully.");
                    return;
                }
                default -> OutputUtil.printError("Invalid menu choice.");
            }
        }
    }
}

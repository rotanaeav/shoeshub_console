package kh.com.shoeshub.features.product;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.features.cart.CartController;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.service.CategoryService;
import kh.com.shoeshub.features.category.service.CategoryServiceImpl;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.features.product.service.ProductService;
import kh.com.shoeshub.features.product.service.ProductServiceImpl;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.features.user.UserController;
import kh.com.shoeshub.features.wishlist.WishlistController;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import kh.com.shoeshub.features.user.UserRole;

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AuthorizationService authorizationService;
    private final WishlistController wishlistController;
    private final CartController cartController;
    private final UserController userController;
    private final ProductUI productUI = new ProductUI();

    public ProductController(AuthorizationService authorizationService, WishlistController wishlistController, CartController cartController, UserController userController) {
        this.authorizationService = authorizationService;
        this.wishlistController = wishlistController;
        this.cartController = cartController;
        this.userController = userController;
        this.productService = new ProductServiceImpl(authorizationService);
        this.categoryService = new CategoryServiceImpl(authorizationService);
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            int choice = productUI.displayProductMenu();

            switch (choice) {
                case 1 -> handleListProducts();
                case 2 -> handleSearchProducts();
                case 3 -> handleFilterByCategory();
                case 4 -> handleViewProductDetail();
                case 5 -> handleCreateProduct();
                case 6 -> handleUpdateProduct();
                case 7 -> handleDeleteProduct();
                case 8 -> handleAddVariant();
                case 9 -> handleUpdateStock();
                case 10 -> handleToggleActive();
                case 0 -> running = false;
            }
        }
    }

    // ------------------------------------------------- customer / guest browse

    // Customer / guest browse: active products only, no STATUS column
    public void handleBrowseProducts(UUID userId) {
        run(() -> {
            List<Product> products = productService.getActiveProducts();

            if (products.isEmpty()) {
                throw new NotFoundException("No products available.");
            }

            Map<Short, String> names = categoryNames();

            productUI.displayProductChoices(products, names);

            int choice = InputUtil.readInt(
                    "Select product number (1-" + products.size() + ", 0 to back)",
                    0,
                    products.size()
            );

            if (choice == 0) {
                return;
            }

            Product product = products.get(choice - 1);

            List<ProductVariant> variants =
                    productService.getVariantsByProductId(product.getId());

            productUI.displayProductDetail(product, variants, names);

            showCustomerProductActions(product, variants, userId);
        });
    }

    private void showCustomerProductActions(
            Product product,
            List<ProductVariant> variants,
            UUID userId
    ) {
        while (true) {
            OutputUtil.println("""
                
                [1] Add to Cart
                [2] Add to Wishlist
                [0] Back
                """);

            int choice = InputUtil.readInt("Choose option", 0, 2);

            switch (choice) {
                case 1 -> handleAddToCart(product, variants, userId);
                case 2 -> handleAddToWishlist(product);
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void handleAddToWishlist(Product product) {
        wishlistController.addToWishlist(product.getId());

        OutputUtil.printSuccess(
                product.getName() + " added to your wishlist."
        );
    }

    private void handleAddToCart(
            Product product,
            List<ProductVariant> variants,
            UUID userId
    ) {
        if (variants == null || variants.isEmpty()) {
            OutputUtil.printError("This product has no variants.");
            return;
        }

        productUI.displayVariants(variants);

        int choice = InputUtil.readInt(
                "Select variant",
                1,
                variants.size()
        );

        ProductVariant variant = variants.get(choice - 1);

        int quantity = InputUtil.readInt(
                "Quantity",
                1,
                variant.getStockQuantity()
        );

        cartController.addToCart(
                userId,
                variant.getId(),
                quantity
        );

        OutputUtil.printSuccess(
                product.getName() + " added to cart successfully."
        );
    }

    // Staff list: everything, with STATUS
    public void handleListProducts() {
        run(() -> showProducts(productService.getAllProducts(), true));
    }

    public void handleSearchProducts() {
        run(() -> {
            String keyword = productUI.readSearchKeyword();
            showProducts(productService.searchProducts(keyword), isStaff());
        });
    }

    public void handleFilterByCategory() {
        run(() -> {
            Short categoryId = productUI.readCategoryId(loadCategories());
            showProducts(productService.getProductsByCategory(categoryId), isStaff());
        });
    }

    // ---------------------------------------------------------------- actions

    public void handleViewProductDetail() {
        run(() -> {
            Map<Short, String> names = categoryNames();
            Product product = pickProduct();
            List<ProductVariant> variants = productService.getVariantsByProductId(product.getId());
            productUI.displayProductDetail(product, variants, names);
        });
    }

    public void handleCreateProduct() {
        run(() -> {
            CreateProductRequest request = productUI.getProductInput(loadCategories());
            Product saved = productService.createProduct(request);
            OutputUtil.printSuccess("Product created with SKU " + saved.getSku());

            if (InputUtil.readConfirm("Add variants (size / color / stock) now?")) {
                addVariantsTo(saved);
            }
        });
    }

    public void handleUpdateProduct() {
        run(() -> {
            Product product = pickProduct();
            OutputUtil.printInfo("Enter the new values for " + product.getSku() + " - " + product.getName());
            CreateProductRequest request = productUI.getProductInput(loadCategories());
            Product updated = productService.updateProduct(product.getId(), request);
            OutputUtil.printSuccess("Product " + updated.getSku() + " updated.");
        });
    }

    public void handleDeleteProduct() {
        run(() -> {
            Product product = pickProduct();
            if (productUI.readConfirmation(
                    "Delete '" + product.getName() + "' and all its variants?")) {

                productService.deleteProduct(product.getId());
                OutputUtil.printSuccess("Product " + product.getSku() + " deleted.");

            } else {
                OutputUtil.printInfo("Delete cancelled.");
            }
        });
    }

    public void handleAddVariant() {
        run(() -> addVariantsTo(pickProduct()));
    }

    public void handleUpdateStock() {
        run(() -> {
            Product product = pickProduct();
            ProductVariant variant = pickVariant(product.getId());
            int newStock = productUI.readStockQuantity();
            productService.updateStock(variant.getId(), newStock);
            OutputUtil.printSuccess("Stock updated to " + newStock + ".");
        });
    }

    public void handleToggleActive() {
        run(() -> {
            Product product = pickProduct();
            boolean newState = !product.isActive();
            String action = newState ? "Activate" : "Deactivate";

            if (productUI.readConfirmation(
                    action + " '" + product.getName() + "'?")) {

                productService.setActive(product.getId(), newState);
                OutputUtil.printSuccess("Product " + product.getSku() + " is now "
                        + (newState ? "ACTIVE" : "INACTIVE") + ".");

            } else {
                OutputUtil.printInfo("Cancelled.");
            }
        });
    }

    // ---------------------------------------------------------------- helpers

    // Runs one menu action: shows errors nicely, then waits for ENTER.
    // SecurityException comes from AuthorizationService (not logged in / no permission).
    private void run(Runnable action) {
        try {
            action.run();
        } catch (AppException | SecurityException e) {
            OutputUtil.printError(e.getMessage());
        } catch (Exception e) {
            OutputUtil.printError("Unexpected error: " + e.getMessage());
        } finally {
            InputUtil.pressEnter();
        }
    }

    private void showProducts(List<Product> products, boolean showStatus) {
        productUI.displayProductsWithVariants(
                products, categoryNames(), productService.getVariantsGroupedByProduct(), showStatus);
    }

    // category id -> category name, used by the tables
    private Map<Short, String> categoryNames() {
        return categoryService.getAllCategories().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private List<Category> loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            throw new NotFoundException("No categories yet. Please create a category first.");
        }
        return categories;
    }

    private Product pickProduct() {
        return productUI.selectProduct(
                productService.getAllProducts(),
                categoryNames()
        );
    }

    private ProductVariant pickVariant(UUID productId) {
        List<ProductVariant> variants =
                productService.getVariantsByProductId(productId);

        return productUI.selectVariant(variants);
    }

    private void addVariantsTo(Product product) {
        do {
            ProductVariant variant = productUI.getVariantInput(product.getId());
            productService.addVariant(variant);
            OutputUtil.printSuccess("Variant added to " + product.getSku() + ".");
        } while (InputUtil.readConfirm("Add another variant?"));
    }
    private boolean isStaff() {
        try {
            authorizationService.requireAnyRole(UserRole.ADMIN, UserRole.SELLER);
            return true;
        } catch (SecurityException e) {
            return false;
        }
    }
}
package kh.com.shoeshub.features.product;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.features.category.Category;
import kh.com.shoeshub.features.category.service.CategoryService;
import kh.com.shoeshub.features.category.service.CategoryServiceImpl;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.features.product.service.ProductService;
import kh.com.shoeshub.features.product.service.ProductServiceImpl;
//import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.features.auth.UserRole;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductUI productUI = new ProductUI();

    // Role checks happen inside the services.
    public ProductController(AuthorizationService authorizationService) {
        this.productService = new ProductServiceImpl(authorizationService);
        this.categoryService = new CategoryServiceImpl(authorizationService);
    }

    // Admin / seller: product management menu loop
    public void showMenu() {
        boolean running = true;
        while (running) {
            productUI.displayProductMenu();
            int choice = InputUtil.readInt("Choose an option", 0, 10);

            switch (choice) {
                case 1 -> handleListProducts();
                case 2 -> handleSearchProducts();
                case 3 -> handleFilterByCategory();
                case 4 -> handleFilterByPrice();
                case 5 -> handleViewProductDetail();
                case 6 -> handleCreateProduct();
                case 7 -> handleUpdateProduct();
                case 8 -> handleDeleteProduct();
                case 9 -> handleAddVariant();
                case 10 -> handleUpdateStock();
                case 0 -> running = false;
            }
        }
    }

    // ------------------------------------------------- customer / guest browse

    // Only ACTIVE products, each with its variants (size / color / stock)
    public void handleBrowseProducts() {
        run(() -> showProducts(productService.getActiveProducts()));
    }

    // ---------------------------------------------------------------- actions

    // Admin view: active and inactive products (the service checks the role)
    public void handleListProducts() {
        run(() -> showProducts(productService.getAllProducts()));
    }

    public void handleSearchProducts() {
        run(() -> {
            String keyword = InputUtil.readRequiredText("Search keyword");
            showProducts(productService.searchProducts(keyword));
        });
    }

    public void handleFilterByCategory() {
        run(() -> {
            Short categoryId = productUI.readCategoryId(loadCategories());
            showProducts(productService.getProductsByCategory(categoryId));
        });
    }

    public void handleFilterByPrice() {
        run(() -> {
            BigDecimal min = BigDecimal.valueOf(InputUtil.readDouble("Minimum price ($)", 0));
            BigDecimal max = BigDecimal.valueOf(InputUtil.readDouble("Maximum price ($)", 0));
            showProducts(productService.filterProductsByPrice(min, max));
        });
    }

    public void handleViewProductDetail() {
        run(() -> {
            Map<Short, String> names = categoryNames();
            Product product = pickProduct(productService.getAllProducts(), names);
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
            if (InputUtil.readConfirm("Delete '" + product.getName() + "' and all its variants?")) {
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
            int newStock = InputUtil.readInt("New stock quantity", 0, 100000);
            productService.updateStock(variant.getId(), newStock);
            OutputUtil.printSuccess("Stock updated to " + newStock + ".");
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

    private void showProducts(List<Product> products) {
        productUI.displayProductsWithVariants(
                products, categoryNames(), productService.getVariantsGroupedByProduct());
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
        return pickProduct(productService.getAllProducts(), categoryNames());
    }

    private Product pickProduct(List<Product> products, Map<Short, String> names) {
        if (products.isEmpty()) {
            throw new NotFoundException("No products available.");
        }
        productUI.displayProductChoices(products, names);
        int choice = InputUtil.readInt("Select product number (1-" + products.size() + ")", 1, products.size());
        return products.get(choice - 1);
    }

    private ProductVariant pickVariant(UUID productId) {
        List<ProductVariant> variants = productService.getVariantsByProductId(productId);
        if (variants.isEmpty()) {
            throw new NotFoundException("This product has no variants yet.");
        }
        productUI.displayVariants(variants);
        int choice = InputUtil.readInt("Select variant number (1-" + variants.size() + ")", 1, variants.size());
        return variants.get(choice - 1);
    }

    private void addVariantsTo(Product product) {
        do {
            ProductVariant variant = productUI.getVariantInput(product.getId());
            productService.addVariant(variant);
            OutputUtil.printSuccess("Variant added to " + product.getSku() + ".");
        } while (InputUtil.readConfirm("Add another variant?"));
    }
}
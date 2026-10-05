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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductController {

    private final ProductService productService = new ProductServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final ProductUI productUI = new ProductUI();

    // Admin / seller: product management menu loop
    public void showMenu() {
        try {
            requireStaff();
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
            InputUtil.pressEnter();
            return;
        }

        boolean running = true;
        while (running) {
            productUI.displayProductMenu();
            int choice = InputUtil.readInt("Choose an option", 0, 11);

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
                case 0 -> running = false;
            }
        }
    }

    // ------------------------------------------------- customer / guest browse

    // Only ACTIVE products, with an option to see sizes and stock
//    public void handleBrowseProducts() {
//        run(() -> {
//            Map<Short, String> names = categoryNames();
//            List<Product> products = productService.getActiveProducts();
//            productUI.displayProducts(products, names);
//
//            if (!products.isEmpty() && InputUtil.readConfirm("View sizes and stock of a product?")) {
//                Product product = pickProduct(products, names);
//                productUI.displayProductDetail(
//                        product, productService.getVariantsByProductId(product.getId()), names);
//            }
//        });
//    }

    public void handleBrowseProducts() {
        run(() -> showProducts(productService.getActiveProducts()));
    }

    // ---------------------------------------------------------------- actions

    // Admin view: active and inactive products
    public void handleListProducts() {
        run(() -> {
            requireStaff();
            showProducts(productService.getAllProducts());
        });
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


    public void handleViewProductDetail() {
        run(() -> {
            requireStaff();
            Map<Short, String> names = categoryNames();
            Product product = pickProduct(productService.getAllProducts(), names);
            List<ProductVariant> variants = productService.getVariantsByProductId(product.getId());
            productUI.displayProductDetail(product, variants, names);
        });
    }

    public void handleCreateProduct() {
        run(() -> {
            requireStaff();
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
            requireStaff();
            Product product = pickProduct();
            OutputUtil.printInfo("Enter the new values for " + product.getSku() + " - " + product.getName());
            CreateProductRequest request = productUI.getProductInput(loadCategories());
            Product updated = productService.updateProduct(product.getId(), request);
            OutputUtil.printSuccess("Product " + updated.getSku() + " updated.");
        });
    }

    public void handleDeleteProduct() {
        run(() -> {
            requireStaff();
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
        run(() -> {
            requireStaff();
            addVariantsTo(pickProduct());
        });
    }

    public void handleUpdateStock() {
        run(() -> {
            requireStaff();
            Product product = pickProduct();
            ProductVariant variant = pickVariant(product.getId());
            int newStock = InputUtil.readInt("New stock quantity", 0, 100000);
            productService.updateStock(variant.getId(), newStock);
            OutputUtil.printSuccess("Stock updated to " + newStock + ".");
        });
    }

//    public void handleLowStock() {
//        run(() -> {
//            requireStaff();
//            int threshold = InputUtil.readInt("Show variants with stock at or below", 0, 100000);
//            List<ProductVariant> variants = productService.getLowStockVariants(threshold);
//
//            if (variants.isEmpty()) {
//                OutputUtil.printInfo("No variants with stock at or below " + threshold + ".");
//                return;
//            }
//            Map<UUID, Product> productsById = productService.getAllProducts().stream()
//                    .collect(Collectors.toMap(Product::getId, p -> p));
////            productUI.displayLowStock(variants, productsById);
//        });
//    }

    // ---------------------------------------------------------------- helpers

    // Runs one menu action: shows errors nicely, then waits for ENTER
    private void run(Runnable action) {
        try {
            action.run();
        } catch (AppException e) {
            OutputUtil.printError(e.getMessage());
        } catch (Exception e) {
            OutputUtil.printError("Unexpected error: " + e.getMessage());
        } finally {
            InputUtil.pressEnter();
        }
    }

    // Only admin and seller may manage products, categories and stock
    private void requireStaff() {
        Session.requireRole(UserRole.ADMIN, UserRole.SELLER);
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

    private void showProducts(List<Product> products) {
        productUI.displayProductsWithVariants(
                products, categoryNames(), productService.getVariantsGroupedByProduct());
    }

}
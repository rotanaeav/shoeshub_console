package kh.com.shoeshub.features.product;

import kh.com.shoeshub.features.product.service.ProductService;
import kh.com.shoeshub.features.product.service.ProductServiceImpl;

public class ProductController {

    private final ProductService productService = new ProductServiceImpl();
    private final ProductUI productUI = new ProductUI();

    public void handleCreateProduct() {
        // TODO: Controller action to create product
    }

    public void handleListProducts() {
        try {
            var products = productService.getAllProducts();
            productUI.displayProducts(products);
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
    }

    public void handleSearchProducts() {
        // TODO: Controller action to search products
    }

    public void handleAddVariant() {
        // TODO: Controller action to add variant
    }

    public void handleUpdateStock() {
        // TODO: Controller action to update stock
    }
}

package kh.com.shoeshub.features.product.service;

import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(CreateProductRequest request);
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByCategory(Short categoryId);
    Product updateProduct(UUID id, CreateProductRequest request);
    void deleteProduct(UUID id);

    ProductVariant addVariant(ProductVariant variant);
    List<ProductVariant> getVariantsByProductId(UUID productId);
    void updateStock(UUID variantId, int newStock);
}

package kh.com.shoeshub.features.product.service;

import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {
    Product createProduct(CreateProductRequest request);
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    List<Product> getActiveProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByCategory(Short categoryId);
    Product updateProduct(UUID id, CreateProductRequest request);
    void deleteProduct(UUID id);

    Product setActive(UUID id, boolean active);

    ProductVariant addVariant(ProductVariant variant);
    List<ProductVariant> getVariantsByProductId(UUID productId);
    void updateStock(UUID variantId, int newStock);
    Map<UUID, List<ProductVariant>> getVariantsGroupedByProduct();
//    List<ProductVariant> getLowStockVariants(int threshold);
}
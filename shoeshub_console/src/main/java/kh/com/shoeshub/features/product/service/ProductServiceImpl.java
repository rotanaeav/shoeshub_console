package kh.com.shoeshub.features.product.service;

import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.dto.CreateProductRequest;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.product.repository.ProductRepositoryImpl;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;
import kh.com.shoeshub.features.product.repository.ProductVariantRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository = new ProductRepositoryImpl();
    private final ProductVariantRepository variantRepository = new ProductVariantRepositoryImpl();

    @Override
    public Product createProduct(CreateProductRequest request) {
        // TODO: Validate and save product
        return null;
    }

    @Override
    public Product getProductById(UUID id) {
        // TODO: Get product by id
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return Collections.emptyList();
    }

    @Override
    public List<Product> getProductsByCategory(Short categoryId) {
        return Collections.emptyList();
    }

    @Override
    public Product updateProduct(UUID id, CreateProductRequest request) {
        // TODO: Update product
        return null;
    }

    @Override
    public void deleteProduct(UUID id) {
        // TODO: Soft delete product
    }

    @Override
    public ProductVariant addVariant(ProductVariant variant) {
        // TODO: Add variant
        return variant;
    }

    @Override
    public List<ProductVariant> getVariantsByProductId(UUID productId) {
        return Collections.emptyList();
    }

    @Override
    public void updateStock(UUID variantId, int newStock) {
        // TODO: Update variant stock
    }
}

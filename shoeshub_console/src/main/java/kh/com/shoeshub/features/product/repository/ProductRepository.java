package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Sample demonstrating Generic CrudRepository usage.
 * Automatically inherits:
 *  - Product save(Product entity)
 *  - Optional<Product> findById(UUID id)
 *  - List<Product> findAll()
 *  - Product update(UUID id, Product entity)
 *  - void deleteById(UUID id)
 */
public interface ProductRepository extends CrudRepository<Product, UUID> {

    // Extra feature-specific query methods defined by the Product feature owner:
    Optional<Product> findBySku(String sku);
    List<Product> findByCategoryId(Short categoryId);
    List<Product> searchByName(String keyword);
}

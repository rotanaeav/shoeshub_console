package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.mapper.ProductMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryImpl implements ProductRepository {

    // Sample of using mapper
    private final RowMapper<Product> productMapper = new ProductMapper();

    @Override
    public Product save(Product product) {
        // TODO: Implement JDBC insert
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products WHERE is_deleted = false ORDER BY created_at DESC";

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return productMapper.mapRows(rs);

        } catch (SQLException e) {
            throw new AppException("Failed to query products from database: " + e.getMessage(), e);
        }
    }

    @Override
    public Product update(UUID id, Product product) {
        // TODO: Implement JDBC update
        return product;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        // TODO: Implement JDBC query by sku
        return Optional.empty();
    }

    @Override
    public List<Product> findByCategoryId(Short categoryId) {
        // TODO: Implement JDBC query by category
        return Collections.emptyList();
    }

    @Override
    public List<Product> searchByName(String keyword) {
        // TODO: Implement JDBC search by name
        return Collections.emptyList();
    }
}

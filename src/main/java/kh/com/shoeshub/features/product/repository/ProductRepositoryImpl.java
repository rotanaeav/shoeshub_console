package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.product.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryImpl implements ProductRepository {

    // --- Inherited from CrudRepository<Product, UUID> ---

    @Override
    public Product save(Product product) {
        // TODO: Implement JDBC PreparedStatement insert
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String sql = """
            SELECT id, category_id, sku, name, description, price, is_active, is_deleted, created_at, updated_at
            FROM products
            WHERE is_deleted = false
            ORDER BY created_at DESC
        """;

        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Failed to query products from database: " + e.getMessage(), e);
        }
        return products;
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

    // --- Custom Product-specific Queries ---

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

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(rs.getObject("id", UUID.class))
                .categoryId(rs.getShort("category_id"))
                .sku(rs.getString("sku"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .active(rs.getBoolean("is_active"))
                .deleted(rs.getBoolean("is_deleted"))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
                .build();
    }
}

package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.mapper.ProductMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryImpl implements ProductRepository {

    private final RowMapper<Product> productMapper = new ProductMapper();

    @Override
    public Product save(Product product) {
        UUID id = UUID.randomUUID();
        String sql = """
                INSERT INTO products (id, category_id, sku, name, description, price, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.setShort(2, product.getCategoryId());
            stmt.setString(3, product.getSku());
            stmt.setString(4, product.getName());
            stmt.setString(5, product.getDescription());
            stmt.setBigDecimal(6, product.getPrice());
            stmt.setBoolean(7, product.isActive());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw toAppException("save product", e);
        }
        return findById(id)
                .orElseThrow(() -> new AppException("Product was saved but could not be loaded."));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        String sql = """
                SELECT * FROM products
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(productMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find product", e);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = """
                SELECT * FROM products
                WHERE is_deleted = FALSE
                ORDER BY created_at DESC;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return productMapper.mapRows(rs);

        } catch (SQLException e) {
            throw toAppException("load products", e);
        }
    }

    @Override
    public Product update(UUID id, Product product) {
        String sql = """
                UPDATE products
                SET category_id = ?, name = ?, description = ?, price = ?,
                    is_active = ?, updated_at = NOW()
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, product.getCategoryId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBoolean(5, product.isActive());
            stmt.setObject(6, id);

            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Product not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("update product", e);
        }
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Override
    public void deleteById(UUID id) {
        String sql = """
                UPDATE products
                SET is_deleted = TRUE, updated_at = NOW()
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Product not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("delete product", e);
        }
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        String sql = """
                SELECT * FROM products
                WHERE sku = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sku);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(productMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find product by SKU", e);
        }
    }

    @Override
    public List<Product> findByCategoryId(Short categoryId) {
        String sql = """
                SELECT * FROM products
                WHERE category_id = ? AND is_deleted = FALSE
                ORDER BY name;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setShort(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return productMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw toAppException("filter products by category", e);
        }
    }

    @Override
    public List<Product> searchByName(String keyword) {
        String sql = """
                SELECT * FROM products
                WHERE name ILIKE ? AND is_deleted = FALSE
                ORDER BY name;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                return productMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw toAppException("search products", e);
        }
    }

    @Override
    public String generateNextSku() {
        String sql = "SELECT nextval('product_sku_seq');";

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            return String.format("SH-%04d", rs.getLong(1));

        } catch (SQLException e) {
            throw toAppException("generate SKU", e);
        }
    }

    private AppException toAppException(String action, SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return new ValidationException("SKU already exists.");
        }
        if ("23503".equals(e.getSQLState())) {
            return new ValidationException("Category does not exist.");
        }
        return new AppException("Failed to " + action + ": " + e.getMessage(), e);
    }
}
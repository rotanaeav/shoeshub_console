package kh.com.shoeshub.features.product.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.mapper.ProductVariantMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductVariantRepositoryImpl implements ProductVariantRepository {

    private final RowMapper<ProductVariant> variantMapper = new ProductVariantMapper();

    @Override
    public ProductVariant save(ProductVariant variant) {
        UUID id = UUID.randomUUID();
        String sql = """
                INSERT INTO product_variants (id, product_id, size, color, stock_quantity)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            stmt.setObject(2, variant.getProductId());
            stmt.setBigDecimal(3, variant.getSize());
            stmt.setString(4, variant.getColor());
            stmt.setInt(5, variant.getStockQuantity());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw toAppException("save variant", e);
        }
        return findById(id)
                .orElseThrow(() -> new AppException("Variant was saved but could not be loaded."));
    }

    @Override
    public Optional<ProductVariant> findById(UUID id) {
        String sql = """
                SELECT * FROM product_variants
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(variantMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find variant", e);
        }
    }

    @Override
    public List<ProductVariant> findAll() {
        String sql = """
                SELECT * FROM product_variants
                WHERE is_deleted = FALSE
                ORDER BY product_id, size;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return variantMapper.mapRows(rs);

        } catch (SQLException e) {
            throw toAppException("load variants", e);
        }
    }

    @Override
    public ProductVariant update(UUID id, ProductVariant variant) {
        String sql = """
                UPDATE product_variants
                SET size = ?, color = ?, stock_quantity = ?
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, variant.getSize());
            stmt.setString(2, variant.getColor());
            stmt.setInt(3, variant.getStockQuantity());
            stmt.setObject(4, id);

            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Variant not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("update variant", e);
        }
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found: " + id));
    }

    @Override
    public void deleteById(UUID id) {
        String sql = """
                UPDATE product_variants
                SET is_deleted = TRUE
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Variant not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("delete variant", e);
        }
    }

    @Override
    public List<ProductVariant> findByProductId(UUID productId) {
        String sql = """
                SELECT * FROM product_variants
                WHERE product_id = ? AND is_deleted = FALSE
                ORDER BY size, color;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return variantMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw toAppException("load variants of product", e);
        }
    }

    @Override
    public ProductVariant updateStock(UUID id, int newStock) {
        String sql = """
                UPDATE product_variants
                SET stock_quantity = ?
                WHERE id = ? AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setObject(2, id);

            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Variant not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("update stock", e);
        }
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found: " + id));
    }
    @Override
    public boolean updateStockWithConnection(Connection conn, UUID id, int quantityToDeduct) throws SQLException {
        String sql = """
                UPDATE product_variants
                SET stock_quantity = stock_quantity - ?
                WHERE id = ? AND is_deleted = FALSE AND stock_quantity >= ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantityToDeduct);
            stmt.setObject(2, id);
            stmt.setInt(3, quantityToDeduct);
            return stmt.executeUpdate() == 1;
        }
    }

    @Override
    public boolean existsVariant(UUID productId, BigDecimal size, String color) {
        String sql = """
                SELECT 1 FROM product_variants
                WHERE product_id = ? AND size = ? AND LOWER(color) = LOWER(?)
                  AND is_deleted = FALSE;
                """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, productId);
            stmt.setBigDecimal(2, size);
            stmt.setString(3, color);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw toAppException("check variant", e);
        }
    }

    @Override
    public Optional<ProductVariant> findDeleted(UUID productId, BigDecimal size, String color) {
        String sql = """
            SELECT * FROM product_variants
            WHERE product_id = ? AND size = ? AND LOWER(color) = LOWER(?)
              AND is_deleted = TRUE
            LIMIT 1;
            """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, productId);
            stmt.setBigDecimal(2, size);
            stmt.setString(3, color);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(variantMapper.mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw toAppException("find deleted variant", e);
        }
    }

    @Override
    public ProductVariant restore(UUID id, String color, int stockQuantity) {
        String sql = """
            UPDATE product_variants
            SET is_deleted = FALSE, color = ?, stock_quantity = ?
            WHERE id = ? AND is_deleted = TRUE;
            """;

        try (Connection conn = DBConfig.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, color);
            stmt.setInt(2, stockQuantity);
            stmt.setObject(3, id);

            if (stmt.executeUpdate() == 0) {
                throw new NotFoundException("Variant not found: " + id);
            }

        } catch (SQLException e) {
            throw toAppException("restore variant", e);
        }
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Variant not found: " + id));
    }
    private AppException toAppException(String action, SQLException e) {
        if ("23503".equals(e.getSQLState())) {
            return new ValidationException("Product does not exist.");
        }
        if ("23514".equals(e.getSQLState())) {
            return new ValidationException("Stock quantity cannot be negative.");
        }
        return new AppException("Failed to " + action + ": " + e.getMessage(), e);
    }
}
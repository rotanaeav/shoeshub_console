package kh.com.shoeshub.features.cart.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.features.cart.CartItem;
import kh.com.shoeshub.features.cart.mapper.CartItemRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CartRepositoryImpl implements CartRepository {

    private final RowMapper<CartItem> rowMapper = new CartItemRowMapper();

    @Override
    public CartItem save(CartItem entity) {

        String sql = """
                INSERT INTO cart_items (
                    user_id,
                    variant_id,
                    quantity
                )
                VALUES (?, ?, ?)
                RETURNING *
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, entity.getUserId());
            ps.setObject(2, entity.getVariantId());
            ps.setInt(3, entity.getQuantity());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CartItem> findById(UUID id) {

        String sql = """
                SELECT
                    id,
                    user_id,
                    variant_id,
                    quantity,
                    is_deleted
                FROM cart_items
                WHERE id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CartItem> findByUserIdAndVariantIdIncludeDeleted(
            UUID userId,
            UUID variantId
    ) {

        String sql = """
            SELECT
                id,
                user_id,
                variant_id,
                quantity,
                is_deleted
            FROM cart_items
            WHERE user_id = ?
            AND variant_id = ?
            """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);
            ps.setObject(2, variantId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CartItem restore(UUID id, int quantity) {

        String sql = """
            UPDATE cart_items
            SET
                quantity = ?,
                is_deleted = FALSE
            WHERE id = ?
            AND is_deleted = TRUE
            RETURNING *
            """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, quantity);
            ps.setObject(2, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CartItem> findByUserIdAndVariantId(
            UUID userId,
            UUID variantId
    ) {

        String sql = """
                SELECT
                    id,
                    user_id,
                    variant_id,
                    quantity,
                    is_deleted
                FROM cart_items
                WHERE user_id = ?
                AND variant_id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);
            ps.setObject(2, variantId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(rowMapper.mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CartItem> findByUserId(UUID userId) {

        String sql = """
                SELECT
                    id,
                    user_id,
                    variant_id,
                    quantity,
                    is_deleted
                FROM cart_items
                WHERE user_id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rowMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CartItem updateQuantity(UUID id, int quantity) {

        String sql = """
                UPDATE cart_items
                SET quantity = ?
                WHERE id = ?
                AND is_deleted = FALSE
                RETURNING *
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, quantity);
            ps.setObject(2, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(UUID id) {

        String sql = """
                UPDATE cart_items
                SET is_deleted = TRUE
                WHERE id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean clearCart(UUID userId) {

        String sql = """
                UPDATE cart_items
                SET is_deleted = TRUE
                WHERE user_id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
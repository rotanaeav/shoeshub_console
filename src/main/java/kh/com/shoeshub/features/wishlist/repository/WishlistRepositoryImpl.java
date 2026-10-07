package kh.com.shoeshub.features.wishlist.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.features.wishlist.Wishlist;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponseRowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WishlistRepositoryImpl implements WishlistRepository {

    private final RowMapper<WishlistResponse> wishlistResponseRowMapper = new WishlistResponseRowMapper();

    @Override
    public Wishlist save(Wishlist wishlist) {

        String sql = """
                INSERT INTO wishlists (
                    user_id,
                    product_id
                )
                VALUES (?, ?)
                RETURNING *
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, wishlist.getUserId());
            ps.setObject(2, wishlist.getProductId());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Wishlist> findByUserIdAndProductId(
            UUID userId,
            UUID productId
    ) {

        String sql = """
                SELECT
                    user_id,
                    product_id,
                    is_deleted
                FROM wishlists
                WHERE user_id = ?
                AND product_id = ?
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);
            ps.setObject(2, productId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Wishlist> findByUserId(UUID userId) {

        String sql = """
                SELECT
                    user_id,
                    product_id,
                    is_deleted
                FROM wishlists
                WHERE user_id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                List<Wishlist> wishlists = new ArrayList<>();

                while (rs.next()) {
                    wishlists.add(mapRow(rs));
                }

                return wishlists;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WishlistResponse> findWishlistDetailsByUserId(UUID userId) {

        String sql = """
            SELECT
                w.product_id,
                p.name AS product_name,
                p.sku,
                p.description,
                p.price

            FROM wishlists w

            JOIN products p
                ON w.product_id = p.id

            WHERE w.user_id = ?
            AND w.is_deleted = FALSE
            AND p.is_deleted = FALSE
            AND p.is_active = TRUE
            """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return wishlistResponseRowMapper.mapRows(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteByUserIdAndProductId(
            UUID userId,
            UUID productId
    ) {

        String sql = """
                UPDATE wishlists
                SET is_deleted = TRUE
                WHERE user_id = ?
                AND product_id = ?
                AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);
            ps.setObject(2, productId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Wishlist restore(
            UUID userId,
            UUID productId
    ) {

        String sql = """
                UPDATE wishlists
                SET is_deleted = FALSE
                WHERE user_id = ?
                AND product_id = ?
                AND is_deleted = TRUE
                RETURNING *
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setObject(1, userId);
            ps.setObject(2, productId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Wishlist mapRow(ResultSet rs) throws SQLException {

        return new Wishlist(
                rs.getObject("user_id", UUID.class),
                rs.getObject("product_id", UUID.class),
                rs.getBoolean("is_deleted")
        );
    }
}
package kh.com.shoeshub.features.review.repository;

import kh.com.shoeshub.features.review.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReviewRepositoryImpl implements ReviewRepository {

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getObject("id", UUID.class));
        r.setProductId(rs.getObject("product_id", UUID.class));
        r.setUserId(rs.getObject("user_id", UUID.class));
        r.setRating(rs.getShort("rating")); r.setComment(rs.getString("comment"));
        r.setDeleted(rs.getBoolean("is_deleted")); r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
    private AppException failure(SQLException e) {
        if ("23505".equals(e.getSQLState()))
            return new ValidationException("You already reviewed this product. Edit your existing review.");
        if ("23503".equals(e.getSQLState()))
            return new ValidationException("The product or customer does not exist.");
        return new AppException("Review database operation failed. Check the database connection.", e);
    }
    private List<Review> query(String sql, Object... values) {
        try (Connection c = DBConfig.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i=0; i<values.length; i++) ps.setObject(i+1, values[i]);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> result = new ArrayList<>();
                while (rs.next()) result.add(map(rs));
                return result;
            }
        } catch (SQLException e) { throw failure(e); }
    }
    private boolean exists(String sql, UUID id) {
        try (Connection c = DBConfig.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1,id);
            try (ResultSet rs=ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw failure(e); }
    }
    @Override
    public boolean isActiveCustomer(UUID id) {
        return exists("SELECT 1 FROM users WHERE id=? AND role='CUSTOMER' AND is_active AND NOT is_deleted",id);
    }
    @Override public boolean productExists(UUID id) {
        return exists("SELECT 1 FROM products WHERE id=? AND is_active AND NOT is_deleted",id);
    }

    @Override
    public Review save(Review r) {
        // A deleted review can be restored; an active duplicate is never silently overwritten.
        String sql = """
            INSERT INTO reviews (product_id,user_id,rating,comment) VALUES (?,?,?,?)
            ON CONFLICT (user_id,product_id) DO UPDATE
            SET rating=EXCLUDED.rating, comment=EXCLUDED.comment, is_deleted=false,
                created_at=CURRENT_TIMESTAMP
            WHERE reviews.is_deleted=true
            RETURNING *
            """;
        try (Connection c=DBConfig.get(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setObject(1,r.getProductId()); ps.setObject(2,r.getUserId());
            ps.setShort(3,r.getRating()); ps.setString(4,r.getComment());
            try (ResultSet rs=ps.executeQuery()) {
                if (!rs.next()) throw new ValidationException("You already reviewed this product. Choose Edit.");
                return map(rs);
            }
        } catch (SQLException e) {
            throw failure(e);
        }
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return query("SELECT * FROM reviews WHERE id=? AND NOT is_deleted",id).stream().findFirst();
    }

    @Override
    public List<Review> findAll() {
        return query("SELECT * FROM reviews WHERE NOT is_deleted ORDER BY created_at DESC,id");
    }

    @Override
    public Review update(UUID id, Review r) {
        try (Connection c=DBConfig.get(); PreparedStatement ps=c.prepareStatement(
                "UPDATE reviews SET rating=?,comment=? WHERE id=? AND user_id=? AND NOT is_deleted RETURNING *")) {
            ps.setShort(1,r.getRating()); ps.setString(2,r.getComment());
            ps.setObject(3,id); ps.setObject(4,r.getUserId());
            try (ResultSet rs=ps.executeQuery()) {
                if (!rs.next()) throw new NotFoundException("Your review was not found.");
                return map(rs);
            }
        } catch (SQLException e) {

            throw failure(e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        delete("UPDATE reviews SET is_deleted=true WHERE id=? AND NOT is_deleted",id);
    }

}

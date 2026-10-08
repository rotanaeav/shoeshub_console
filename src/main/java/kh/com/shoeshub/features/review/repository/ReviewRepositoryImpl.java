package kh.com.shoeshub.features.review.repository;

import java.sql.*;
import java.util.*;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.*;
import kh.com.shoeshub.features.review.Review;
import kh.com.shoeshub.features.review.mapper.ReviewMapper;

public class ReviewRepositoryImpl implements ReviewRepository {
  private final ReviewMapper mapper = new ReviewMapper();

  private List<Review> query(String sql, Object... params) {
    try (Connection c = DBConfig.get();
        PreparedStatement s = c.prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) s.setObject(i + 1, params[i]);
      try (ResultSet rs = s.executeQuery()) {
        return mapper.mapRows(rs);
      }
    } catch (SQLException e) {
      throw new AppException("Cannot read reviews.", e);
    }
  }

  public Review save(Review review) {
    // UNIQUE(user_id, product_id): restore a deleted review; reject an active duplicate atomically.
    String sql =
        """
        INSERT INTO reviews(product_id,user_id,rating,comment) VALUES(?,?,?,?)
        ON CONFLICT(user_id,product_id) DO UPDATE
        SET rating=EXCLUDED.rating,comment=EXCLUDED.comment,is_deleted=false,created_at=CURRENT_TIMESTAMP
        WHERE reviews.is_deleted=true RETURNING *
        """;
    try (Connection c = DBConfig.get();
        PreparedStatement s = c.prepareStatement(sql)) {
      s.setObject(1, review.getProductId());
      s.setObject(2, review.getUserId());
      s.setShort(3, review.getRating());
      s.setString(4, review.getComment());
      try (ResultSet rs = s.executeQuery()) {
        if (!rs.next())
          throw new BusinessException(
              "You already reviewed this product. Edit your review instead.");
        return mapper.mapRow(rs);
      }
    } catch (SQLException e) {
      throw new AppException("Cannot save review.", e);
    }
  }

  private static final String SELECT_WITH_JOINS =
      """
      SELECT r.*, u.username AS user_name, p.name AS product_name
      FROM reviews r
      JOIN users u ON u.id = r.user_id
      JOIN products p ON p.id = r.product_id
      """;

  public Optional<Review> findById(UUID id) {
    return query(SELECT_WITH_JOINS + " WHERE r.id=? AND NOT r.is_deleted", id).stream().findFirst();
  }

  public List<Review> findAll() {
    return query(SELECT_WITH_JOINS + " WHERE NOT r.is_deleted ORDER BY r.created_at DESC, r.id");
  }

  public List<Review> findByProductId(UUID id) {
    return query(
        SELECT_WITH_JOINS + " WHERE r.product_id=? AND NOT r.is_deleted ORDER BY r.created_at DESC, r.id",
        id);
  }

  public List<Review> findByUserId(UUID id) {
    return query(
        SELECT_WITH_JOINS + " WHERE r.user_id=? AND NOT r.is_deleted ORDER BY r.created_at DESC, r.id",
        id);
  }

  public double getAverageRating(UUID id) {
    try (Connection c = DBConfig.get();
        PreparedStatement s =
            c.prepareStatement(
                "SELECT COALESCE(AVG(rating),0) FROM reviews WHERE product_id=? AND NOT"
                    + " is_deleted")) {
      s.setObject(1, id);
      try (ResultSet rs = s.executeQuery()) {
        rs.next();
        return rs.getDouble(1);
      }
    } catch (SQLException e) {
      throw new AppException("Cannot read average rating.", e);
    }
  }

  public Review update(UUID id, Review review) {
    var rows =
        query(
            "UPDATE reviews SET rating=?,comment=? WHERE id=? AND NOT is_deleted RETURNING *",
            review.getRating(),
            review.getComment(),
            id);
    return rows.stream().findFirst().orElseThrow(() -> new NotFoundException("Review not found."));
  }

  public void deleteById(UUID id) {
    if (query("UPDATE reviews SET is_deleted=true WHERE id=? AND NOT is_deleted RETURNING *", id)
        .isEmpty()) throw new NotFoundException("Review not found.");
  }
}

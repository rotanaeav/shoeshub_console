package kh.com.shoeshub.features.review.mapper;

import java.sql.*;
import java.util.UUID;
import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.review.Review;

public class ReviewMapper implements RowMapper<Review> {
  private String getOptionalString(ResultSet rs, String column) {
    try {
      return rs.getString(column);
    } catch (SQLException e) {
      return null;
    }
  }

  public Review mapRow(ResultSet rs) throws SQLException {
    return Review.builder()
        .id(rs.getObject("id", UUID.class))
        .productId(rs.getObject("product_id", UUID.class))
        .userId(rs.getObject("user_id", UUID.class))
        .rating(rs.getShort("rating"))
        .comment(rs.getString("comment"))
        .deleted(rs.getBoolean("is_deleted"))
        .createdAt(rs.getTimestamp("created_at"))
        .userName(getOptionalString(rs, "user_name"))
        .productName(getOptionalString(rs, "product_name"))
        .build();
  }
}

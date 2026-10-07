package kh.com.shoeshub.features.wishlist.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.wishlist.Wishlist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WishlistItemRowMapper implements RowMapper<Wishlist> {

    @Override
    public Wishlist mapRow(ResultSet rs) throws SQLException {
        return Wishlist.builder()
                .userId(rs.getObject("user_id", UUID.class))
                .productId(rs.getObject("product_id", UUID.class))
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}
package kh.com.shoeshub.features.cart.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.cart.CartItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CartItemRowMapper implements RowMapper<CartItem> {

    @Override
    public CartItem mapRow(ResultSet rs) throws SQLException {
        return CartItem.builder()
                .id(rs.getObject("id", UUID.class))
                .userId(rs.getObject("user_id", UUID.class))
                .variantId(rs.getObject("variant_id", UUID.class))
                .quantity(rs.getInt("quantity"))
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}

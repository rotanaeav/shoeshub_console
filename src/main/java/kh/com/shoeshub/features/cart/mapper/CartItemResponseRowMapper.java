package kh.com.shoeshub.features.cart.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.cart.dto.CartItemResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CartItemResponseRowMapper implements RowMapper<CartItemResponse> {

    @Override
    public CartItemResponse mapRow(ResultSet rs) throws SQLException {

        return new CartItemResponse(
                rs.getObject("cart_item_id", UUID.class),
                rs.getObject("variant_id", UUID.class),
                rs.getString("product_name"),
                rs.getBigDecimal("size"),
                rs.getString("color"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity")
        );
    }
}

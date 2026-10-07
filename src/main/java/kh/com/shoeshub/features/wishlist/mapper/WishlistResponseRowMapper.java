package kh.com.shoeshub.features.wishlist.mapper;

import kh.com.shoeshub.common.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WishlistResponseRowMapper
        implements RowMapper<WishlistResponse> {

    @Override
    public WishlistResponse mapRow(ResultSet rs) throws SQLException {

        return new WishlistResponse(
                rs.getObject("product_id", UUID.class),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getString("description"),
                rs.getBigDecimal("price")
        );
    }
}

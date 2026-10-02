package kh.com.shoeshub.features.product.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.product.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

// Sample of using RowMapper
public class ProductMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(rs.getObject("id", UUID.class))
                .categoryId(rs.getShort("category_id"))
                .sku(rs.getString("sku"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .active(rs.getBoolean("is_active"))
                .deleted(rs.getBoolean("is_deleted"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }
}

package kh.com.shoeshub.features.product.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.product.ProductVariant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ProductVariantMapper implements RowMapper<ProductVariant> {

    @Override
    public ProductVariant mapRow(ResultSet rs) throws SQLException {
        return ProductVariant.builder()
                .id(rs.getObject("id", UUID.class))
                .productId(rs.getObject("product_id", UUID.class))
                .size(rs.getBigDecimal("size"))
                .color(rs.getString("color"))
                .stockQuantity(rs.getInt("stock_quantity"))
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}

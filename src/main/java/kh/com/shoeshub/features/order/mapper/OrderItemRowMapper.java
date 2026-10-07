package kh.com.shoeshub.features.order.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.order.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OrderItemRowMapper implements RowMapper<OrderItem> {

    @Override
    public OrderItem mapRow(ResultSet rs) throws SQLException {
        return OrderItem.builder()
                .id(rs.getObject("id", UUID.class))
                .orderId(rs.getObject("order_id", UUID.class))
                .variantId(rs.getObject("variant_id", UUID.class))
                .quantity(rs.getInt("quantity"))
                .unitPrice(rs.getBigDecimal("unit_price"))
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}

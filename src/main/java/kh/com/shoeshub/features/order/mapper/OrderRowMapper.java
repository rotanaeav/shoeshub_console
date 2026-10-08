package kh.com.shoeshub.features.order.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs) throws SQLException {
        return Order.builder()
                .id(rs.getObject("id", UUID.class))
                .customerId(rs.getObject("customer_id", UUID.class))
                .status(OrderStatus.valueOf(rs.getString("status")))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .deleted(rs.getBoolean("is_deleted"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }
}

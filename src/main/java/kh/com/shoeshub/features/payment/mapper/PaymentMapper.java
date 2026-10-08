package kh.com.shoeshub.features.payment.mapper;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.features.payment.Payment;
import kh.com.shoeshub.features.payment.PaymentMethod;
import kh.com.shoeshub.features.payment.PaymentStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/* Converts a database row into a Payment. Layer: Mapper. */
public class PaymentMapper implements RowMapper<Payment> {

    /** Maps a ResultSet row to a Payment object. */
    @Override
    public Payment mapRow(ResultSet rs) throws SQLException {
        return Payment.builder()
                .id(rs.getObject("id", UUID.class))
                .orderId(rs.getObject("order_id", UUID.class))
                .method(PaymentMethod.valueOf(rs.getString("method")))
                .amount(rs.getBigDecimal("amount"))
                .status(PaymentStatus.valueOf(rs.getString("status")))
                .deleted(rs.getBoolean("is_deleted"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }
}

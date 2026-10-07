package kh.com.shoeshub.features.order.dto.response;

import kh.com.shoeshub.features.order.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        Timestamp createdAt,
        Timestamp updateAt,
        List<OrderItemResponse> items) {
}

package kh.com.shoeshub.features.order.dto.request;

import kh.com.shoeshub.features.order.OrderStatus;

import java.util.UUID;

public record UpdateOrderStatusRequest(
        UUID orderId,
        OrderStatus newStatus
) {
}

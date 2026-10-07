package kh.com.shoeshub.features.order;

import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;

public class OrderMapper {

    public static OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getItems().stream().map(OrderMapper::toItemResponse).toList()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getVariantId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

}

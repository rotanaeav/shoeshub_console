package kh.com.shoeshub.features.order;

import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;

import java.util.Collections;
import java.util.List;

public class OrderMapper {

    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(OrderMapper::toItemResponse).toList()
                : Collections.emptyList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResponse(
                item.getId(),
                item.getVariantId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}

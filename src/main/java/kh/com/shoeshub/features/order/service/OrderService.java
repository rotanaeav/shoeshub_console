package kh.com.shoeshub.features.order.service;

import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order checkout(UUID customerId);
    Order placeOrder(Order order);
    List<Order> getMyOrders(UUID customerId);
    OrderResponse getOrderDetail(UUID orderId, UUID requesterId, boolean isAdmin);
    void cancelOrder(UUID orderId, UUID customerId, boolean isAdmin);
    List<Order> getAllOrders(OrderStatus statusFilter);
    void updateStatus(UUID orderId, OrderStatus newStatus);
    Order getOrderById(UUID orderId);
}
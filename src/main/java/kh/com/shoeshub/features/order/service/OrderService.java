package kh.com.shoeshub.features.order.service;

import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    // TODO: Define order & checkout service methods (e.g. checkout, getOrderById, getOrdersByCustomer)
    Order placeOrder(Order order);
    List<Order> getMyOrders(UUID customerId);
    Order getOrderDetail(UUID orderId, UUID requesterId, boolean admin);
    void cancelOrder(UUID orderId, UUID customerId);
    List<Order> getAllOrders(OrderStatus statusFilter);
    void updateStatus(UUID orderId, OrderStatus newStatus);

}

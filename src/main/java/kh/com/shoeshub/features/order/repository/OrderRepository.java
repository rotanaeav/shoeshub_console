package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderItem;
import kh.com.shoeshub.features.order.OrderStatus;
import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {

    Order save(Order order, Connection conn);

    OrderItem saveOrderItem(OrderItem item, Connection conn);

    List<OrderItem> findOrderItemsByOrderId(UUID orderId);

    List<OrderItemResponse> findOrderItemsWithDetails(UUID orderId);

    boolean updateStatus(UUID id, OrderStatus status);

    boolean updateStatus(UUID id, OrderStatus status, Connection conn);

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findAllByStatus(OrderStatus status);
}

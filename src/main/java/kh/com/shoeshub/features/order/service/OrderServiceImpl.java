package kh.com.shoeshub.features.order.service;

import kh.com.shoeshub.exception.BusinessException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;
import kh.com.shoeshub.features.order.repository.OrderRepository;
import kh.com.shoeshub.features.order.repository.OrderRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();

//    public OrderServiceImpl(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }

    private Order findOrderOrThrow(UUID orderId) {
        Optional<Order> found = orderRepository.findById(orderId);
        if (found.isEmpty()) {
            throw new NotFoundException("Order not found.");
        }
        return found.get();
    }

    // The only allowed status steps
    private boolean isAllowedStep(OrderStatus from, OrderStatus to) {
        if (from == OrderStatus.PENDING && to == OrderStatus.PAID) {
            return true;
        }
        if (from == OrderStatus.PAID && to == OrderStatus.SHIPPED) {
            return true;
        }
        if (from == OrderStatus.SHIPPED && to == OrderStatus.DELIVERED) {
            return true;
        }
        return false;
    }

    @Override
    public Order placeOrder(Order order) {
        // rule: an order must have at least one item
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("An order needs at least one item.");
        }

        order.setId(UUID.randomUUID());   // give it a new id
        order.setStatus(OrderStatus.PENDING);        // every new order starts as PENDING

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getMyOrders(UUID customerId) {
        List<Order> myOrders = new ArrayList<>();

        for (Order order : orderRepository.findAll()) {
            if (order.getCustomerId().equals(customerId)) {
                myOrders.add(order);
            }
        }
        return myOrders;
    }

    @Override
    public Order getOrderDetail(UUID orderId, UUID requesterId, boolean admin) {
        Order order = findOrderOrThrow(orderId);

        // rule: only the owner or an admin can see it
        if (!admin && !order.getCustomerId().equals(requesterId)) {
            throw new BusinessException("This is not your order.");
        }
        return order;
    }

    @Override
    public void cancelOrder(UUID orderId, UUID customerId) {
        Order order = findOrderOrThrow(orderId);

        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException("This is not your order.");
        }
        // rule: only PENDING orders can be cancelled
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be cancelled.");
        }

        orderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
    }

    @Override
    public List<Order> getAllOrders(OrderStatus statusFilter) {
        if (statusFilter == null) {
            return orderRepository.findAll();              // no filter = everything
        }

        List<Order> result = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == statusFilter) {
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public void updateStatus(UUID orderId, OrderStatus newStatus) {
        Order order = findOrderOrThrow(orderId);

        // rule: only allowed steps
        if (!isAllowedStep(order.getStatus(), newStatus)) {
            throw new BusinessException("Cannot change " + order.getStatus() + " -> " + newStatus);
        }

        orderRepository.updateStatus(orderId, newStatus);
    }
}

package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;

import java.util.*;

public class OrderRepositoryImpl implements OrderRepository {

    private final Map<UUID, Order> orderMap = new HashMap<>();

    @Override
    public Order save(Order order) {
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(orderMap.get(id));
    }

    @Override
    public List<Order> findAll() {
        return orderMap.values().stream().toList();
    }

    @Override
    public Order update(UUID id, Order order) {
        if (!orderMap.containsKey(id)) {
            return null;                 // nothing to update; the service decides what error to show
        }
        order.setId(id);                 // keep the id and the map key the same
        orderMap.put(id, order);
        return order;
    }

    @Override
    public void deleteById(UUID id) {
        orderMap.remove(id);
    }

    @Override
    public boolean updateStatus(UUID id, OrderStatus status) {
        Order order = orderMap.get(id);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return true;
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getCustomerId().equals(customerId)) {
                result.add(order);
            }
        }
        return result;
    }
}
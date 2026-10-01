package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.features.order.Order;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order save(Order entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public Order update(UUID id, Order entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }
}

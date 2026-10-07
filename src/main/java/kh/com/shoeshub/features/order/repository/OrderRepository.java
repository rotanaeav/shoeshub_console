package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    // TODO: Define custom order queries (e.g. findByCustomerId, findItemsByOrderId, updateStatus)

        // save, findById, findAll, update and deleteById already come from CrudRepository

        boolean updateStatus(UUID id, OrderStatus status);

        List<Order> findByCustomerId(UUID customerId);
    }


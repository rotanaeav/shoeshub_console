package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.order.Order;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    // TODO: Define custom order queries (e.g. findByCustomerId, findItemsByOrderId, updateStatus)
}

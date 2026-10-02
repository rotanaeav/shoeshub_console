package kh.com.shoeshub.features.order.service;

import kh.com.shoeshub.features.order.repository.OrderRepository;
import kh.com.shoeshub.features.order.repository.OrderRepositoryImpl;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    // TODO: Implement order business logic and checkout transaction
}

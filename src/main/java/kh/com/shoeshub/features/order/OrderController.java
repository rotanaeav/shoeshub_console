package kh.com.shoeshub.features.order;

import kh.com.shoeshub.features.order.service.OrderService;
import kh.com.shoeshub.features.order.service.OrderServiceImpl;

public class OrderController {

    private final OrderService orderService = new OrderServiceImpl();
    private final OrderUI orderUI = new OrderUI();

    public void handleViewOrderHistory() {
        // TODO: Implement view order history
    }

    public void handleUpdateOrderStatus() {
        // TODO: Implement update order status
    }
}

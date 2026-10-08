package kh.com.shoeshub.features.order;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.features.order.service.OrderService;
import kh.com.shoeshub.features.order.service.OrderServiceImpl;

import java.util.List;
import java.util.UUID;

public class OrderController {

    private final AuthorizationService authorizationService;
    private final OrderService orderService;
    private final OrderUI orderUI = new OrderUI();

    public OrderController(AuthorizationService authorizationService, OrderService orderService) {
        this.authorizationService = authorizationService;
        this.orderService = orderService;
    }

    public OrderController() {
        this(null, new OrderServiceImpl());
    }

    public void handleViewOrderHistory() {
        // TODO: Implement view order history
        try {
            // TEMP: replace with the logged-in user from your friend's login feature
            UUID customerId = orderUI.readUuid("Your user id: ");

            List<Order> orders = orderService.getMyOrders(customerId);
            orderUI.printOrders(orders);
        } catch (RuntimeException e) {
            orderUI.printMessage("Error: " + e.getMessage());
        }
    }

    public void handleUpdateOrderStatus() {
        // TODO: Implement update order status
        try {
            UUID orderId = orderUI.readUuid("Order id: ");
            OrderStatus newStatus = orderUI.readStatus();

            orderService.updateStatus(orderId, newStatus);
            orderUI.printMessage("Status updated.");
        } catch (RuntimeException e) {
            // the service throws errors like "Order not found." and we just show them
            orderUI.printMessage("Error: " + e.getMessage());
        }
    }
}

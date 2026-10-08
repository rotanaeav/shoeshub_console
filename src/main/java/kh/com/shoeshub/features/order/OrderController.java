package kh.com.shoeshub.features.order;

import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;
import kh.com.shoeshub.features.order.service.OrderService;
import kh.com.shoeshub.features.order.service.OrderServiceImpl;
import kh.com.shoeshub.features.user.UserRole;
import kh.com.shoeshub.utils.InputUtil;
import kh.com.shoeshub.utils.OutputUtil;

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
        try {
            AuthenticatedUser currentUser = authorizationService != null
                    ? authorizationService.requireAuthenticated()
                    : null;

            if (currentUser == null) {
                OutputUtil.printError("You must be logged in to view order history.");
                return;
            }

            while (true) {
                List<Order> orders = orderService.getMyOrders(currentUser.id());
                if (orders.isEmpty()) {
                    OutputUtil.printInfo("You have no past orders yet.");
                    InputUtil.pressEnter();
                    return;
                }

                orderUI.displayOrderList(orders, "MY ORDER HISTORY");
                OutputUtil.println(" [0] Back");

                int choice = InputUtil.readInt("Select order # to view details (0 to back)", 0, orders.size());
                if (choice == 0) {
                    return;
                }

                Order selected = orders.get(choice - 1);
                OrderResponse detail = orderService.getOrderDetail(selected.getId(), currentUser.id(), false);
                orderUI.displayOrderDetail(detail);

                if (selected.getStatus() == OrderStatus.PENDING) {
                    OutputUtil.println("\n [1] Cancel This Order");
                    OutputUtil.println(" [0] Back");
                    int action = InputUtil.readInt("Choose option", 0, 1);
                    if (action == 1) {
                        boolean confirmed = InputUtil.readConfirm("Are you sure you want to cancel this order?");
                        if (confirmed) {
                            orderService.cancelOrder(selected.getId(), currentUser.id(), false);
                            OutputUtil.printSuccess("Order cancelled successfully. Stock has been restored.");
                            InputUtil.pressEnter();
                        }
                    }
                } else {
                    InputUtil.pressEnter();
                }
            }

        } catch (Exception e) {
            OutputUtil.printError(e.getMessage() != null ? e.getMessage() : "An error occurred.");
            InputUtil.pressEnter();
        }
    }

    public void handleOrderManagement() {
        try {
            if (authorizationService != null) {
                authorizationService.requireAnyRole(UserRole.ADMIN, UserRole.SELLER);
            }

            while (true) {
                OutputUtil.printHeader("ORDER MANAGEMENT");
                OutputUtil.println("""
                        [1] View All Orders
                        [2] Filter Orders by Status
                        [3] Update Order Status
                        [0] Back
                        """);

                int choice = InputUtil.readInt("Choose menu", 0, 3);

                switch (choice) {
                    case 1 -> viewAllOrders();
                    case 2 -> filterOrdersByStatus();
                    case 3 -> handleUpdateOrderStatus();
                    case 0 -> {
                        return;
                    }
                }
            }

        } catch (Exception e) {
            OutputUtil.printError(e.getMessage() != null ? e.getMessage() : "An error occurred.");
            InputUtil.pressEnter();
        }
    }

    private void viewAllOrders() {
        List<Order> orders = orderService.getAllOrders(null);
        if (orders.isEmpty()) {
            OutputUtil.printInfo("No orders found in the system.");
            InputUtil.pressEnter();
            return;
        }

        while (true) {
            orderUI.displayOrderList(orders, "ALL ORDERS");
            OutputUtil.println(" [0] Back");

            int choice = InputUtil.readInt("Select order # to view details (0 to back)", 0, orders.size());
            if (choice == 0) {
                return;
            }

            Order selected = orders.get(choice - 1);
            OrderResponse detail = orderService.getOrderDetail(selected.getId(), null, true);
            orderUI.displayOrderDetail(detail);
            InputUtil.pressEnter();
        }
    }

    private void filterOrdersByStatus() {
        OutputUtil.printSubHeader("FILTER BY STATUS");
        OutputUtil.println("""
                [1] PENDING
                [2] PAID
                [3] CANCELLED
                [0] Cancel
                """);

        int choice = InputUtil.readInt("Choose status", 0, 3);
        if (choice == 0) return;

        OrderStatus status = switch (choice) {
            case 1 -> OrderStatus.PENDING;
            case 2 -> OrderStatus.PAID;
            case 3 -> OrderStatus.CANCELLED;
            default -> null;
        };

        if (status == null) return;

        List<Order> orders = orderService.getAllOrders(status);
        if (orders.isEmpty()) {
            OutputUtil.printInfo("No orders found with status " + status.name());
            InputUtil.pressEnter();
            return;
        }

        orderUI.displayOrderList(orders, "ORDERS - " + status.name());
        InputUtil.pressEnter();
    }

    public void handleUpdateOrderStatus() {
        try {
            if (authorizationService != null) {
                authorizationService.requireAnyRole(UserRole.ADMIN, UserRole.SELLER);
            }

            List<Order> orders = orderService.getAllOrders(null);
            if (orders.isEmpty()) {
                OutputUtil.printInfo("No orders found to update.");
                InputUtil.pressEnter();
                return;
            }

            orderUI.displayOrderList(orders, "SELECT ORDER TO UPDATE");
            OutputUtil.println(" [0] Cancel");

            int choice = InputUtil.readInt("Select order # to update status (0 to cancel)", 0, orders.size());
            if (choice == 0) {
                OutputUtil.println("Update cancelled.");
                return;
            }

            Order selected = orders.get(choice - 1);
            OutputUtil.println("\nSelected Order: " + selected.getId() + " | Current Status: " + selected.getStatus());

            if (selected.getStatus() == OrderStatus.PAID || selected.getStatus() == OrderStatus.CANCELLED) {
                OutputUtil.printWarning("Cannot change status from terminal state: " + selected.getStatus());
                InputUtil.pressEnter();
                return;
            }

            OutputUtil.println("""
                    [1] Mark as PAID
                    [2] Mark as CANCELLED
                    [0] Cancel
                    """);

            int statusChoice = InputUtil.readInt("Choose new status", 0, 2);
            if (statusChoice == 0) {
                OutputUtil.println("Status update cancelled.");
                return;
            }

            OrderStatus newStatus = (statusChoice == 1) ? OrderStatus.PAID : OrderStatus.CANCELLED;
            boolean confirmed = InputUtil.readConfirm("Are you sure you want to change order status to " + newStatus + "?");
            if (!confirmed) {
                OutputUtil.println("Status update cancelled.");
                return;
            }

            orderService.updateStatus(selected.getId(), newStatus);
            OutputUtil.printSuccess("Order " + selected.getId() + " updated to " + newStatus + " successfully.");
            InputUtil.pressEnter();

        } catch (Exception e) {
            OutputUtil.printError(e.getMessage() != null ? e.getMessage() : "An error occurred.");
            InputUtil.pressEnter();
        }
    }

    public Order handleCheckout(UUID customerId) {
        try {
            Order order = orderService.checkout(customerId);
            OrderResponse detail = orderService.getOrderDetail(order.getId(), customerId, false);
            orderUI.displayReceipt(order, detail);
            return order;
        } catch (Exception e) {
            OutputUtil.printError(e.getMessage() != null ? e.getMessage() : "Checkout failed.");
            return null;
        }
    }
}

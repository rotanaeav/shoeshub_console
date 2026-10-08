package kh.com.shoeshub.features.order;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class OrderUI {

    // TODO: Implement order console UI views and menus

    private final Scanner scanner = new Scanner(System.in);

    // Ask a question, read one UUID  (used on lines 18, 30)
    public UUID readUuid(String label) {
        System.out.print(label);
        return UUID.fromString(scanner.nextLine().trim());
    }

    // Ask for the new status  (used on line 31)
    public OrderStatus readStatus() {
        System.out.print("New status (PAID / CANCELLED): ");
        return OrderStatus.valueOf(scanner.nextLine().trim().toUpperCase());
    }

    // Show a list of orders  (used on line 21)
    public void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        for (Order order : orders) {
            System.out.println(order.getId() + " | " + order.getStatus()
                    + " | items: " + order.getItems().size());
        }
    }

    // Show one message  (used on lines 23, 34, 37)
    public void printMessage(String message) {
        System.out.println(message);
    }


}

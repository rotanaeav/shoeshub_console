package kh.com.shoeshub.features.order.export;

import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderSummaryExporter {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public Path exportSummary(Order order, OrderResponse details) {
        if (order == null) {
            throw new AppException("Order cannot be null for export");
        }

        try {
            Path exportsDir = Path.of("exports");
            if (!Files.exists(exportsDir)) {
                Files.createDirectories(exportsDir);
            }

            String filename = "order_summary_" +
                    order.getId().toString().substring(0, 8) + "_" +
                    LocalDateTime.now().format(FILE_TIMESTAMP) + ".txt";
            Path target = exportsDir.resolve(filename);

            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("           SHOESHUB ORDER SUMMARY       \n");
            sb.append("========================================\n");
            sb.append("Order ID    : ").append(order.getId()).append("\n");
            sb.append("Date        : ").append(order.getCreatedAt() != null ?
                    order.getCreatedAt().toString().substring(0, 19) : "-").append("\n");
            sb.append("Status      : ").append(order.getStatus() != null ? order.getStatus().name() : "-").append("\n");
            sb.append("Customer ID : ").append(order.getCustomerId()).append("\n");
            sb.append("----------------------------------------\n");
            sb.append("ITEMS:\n");

            if (details != null && details.items() != null && !details.items().isEmpty()) {
                List<OrderItemResponse> items = details.items();
                for (int i = 0; i < items.size(); i++) {
                    OrderItemResponse item = items.get(i);
                    sb.append(String.format("  %d. %s%n", i + 1,
                            item.productName() != null ? item.productName() : "Product"));
                    sb.append(String.format("     Size: %s, Color: %s, Qty: %d%n",
                            item.size() != null ? item.size().toString() : "-",
                            item.color() != null ? item.color() : "-",
                            item.quantity()));
                    sb.append(String.format("     Unit Price: $%.2f, Subtotal: $%.2f%n",
                            item.unitPrice() != null ? item.unitPrice().doubleValue() : 0.0,
                            item.subtotal() != null ? item.subtotal().doubleValue() : 0.0));
                }
            } else {
                sb.append("  (No item details recorded)\n");
            }

            sb.append("----------------------------------------\n");
            sb.append(String.format("TOTAL AMOUNT: $%.2f%n",
                    order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0));
            sb.append("========================================\n");
            sb.append("Thank you for shopping with ShoesHub!\n");
            sb.append("========================================\n");

            try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
                writer.write(sb.toString());
            }

            return target;
        } catch (IOException e) {
            throw new AppException("Failed to export order summary: " + e.getMessage(), e);
        }
    }
}

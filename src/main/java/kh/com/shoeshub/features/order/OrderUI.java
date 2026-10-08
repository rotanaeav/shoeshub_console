package kh.com.shoeshub.features.order;

import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;
import kh.com.shoeshub.utils.ColorUtil;
import kh.com.shoeshub.utils.OutputUtil;
import kh.com.shoeshub.utils.TableUtil;
import org.nocrala.tools.texttablefmt.Table;

import java.math.BigDecimal;
import java.util.List;

public class OrderUI {

    public void displayOrderList(List<Order> orders, String title) {
        OutputUtil.printSubHeader(title);

        Table table = TableUtil.createTable(5, "#", "ORDER ID", "DATE", "TOTAL ($)", "STATUS");

        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String shortId = o.getId().toString().substring(0, 8) + "...";
            String date = o.getCreatedAt() != null ? o.getCreatedAt().toString().substring(0, 19) : "-";
            String total = o.getTotalAmount() != null ? String.format("%.2f", o.getTotalAmount()) : "0.00";

            String statusColor = switch (o.getStatus()) {
                case PAID -> ColorUtil.GREEN;
                case PENDING -> ColorUtil.YELLOW;
                case CANCELLED -> ColorUtil.RED;
            };
            String statusStr = statusColor + o.getStatus().name() + ColorUtil.RESET;

            table.addCell(String.valueOf(i + 1));
            table.addCell(shortId);
            table.addCell(date);
            table.addCell(total);
            table.addCell(statusStr);
        }

        TableUtil.render(table);
    }

    public void displayOrderDetail(OrderResponse order) {
        OutputUtil.printHeader("ORDER DETAILS");

        OutputUtil.println("Order ID     : " + ColorUtil.CYAN + order.id() + ColorUtil.RESET);
        OutputUtil.println("Customer ID  : " + order.customerId());
        OutputUtil.println("Order Date   : " + (order.createdAt() != null ? order.createdAt().toString().substring(0, 19) : "-"));

        String statusColor = switch (order.status()) {
            case PAID -> ColorUtil.GREEN;
            case PENDING -> ColorUtil.YELLOW;
            case CANCELLED -> ColorUtil.RED;
        };
        OutputUtil.println("Status       : " + statusColor + order.status().name() + ColorUtil.RESET);
        OutputUtil.println("Total Amount : " + ColorUtil.BOLD + "$" + String.format("%.2f", order.totalAmount()) + ColorUtil.RESET);

        OutputUtil.printSubHeader("ORDERED ITEMS");

        List<OrderItemResponse> items = order.items();
        if (items == null || items.isEmpty()) {
            OutputUtil.printInfo("No item details found for this order.");
            return;
        }

        Table table = TableUtil.createTable(7, "#", "PRODUCT NAME", "SIZE", "COLOR", "QTY", "PRICE ($)", "SUBTOTAL ($)");

        for (int i = 0; i < items.size(); i++) {
            OrderItemResponse item = items.get(i);
            table.addCell(String.valueOf(i + 1));
            table.addCell(item.productName() != null ? item.productName() : "Product");
            table.addCell(item.size() != null ? item.size().toString() : "-");
            table.addCell(item.color() != null ? item.color() : "-");
            table.addCell(String.valueOf(item.quantity()));
            table.addCell(item.unitPrice() != null ? String.format("%.2f", item.unitPrice()) : "0.00");
            table.addCell(item.subtotal() != null ? String.format("%.2f", item.subtotal()) : "0.00");
        }

        TableUtil.render(table);
    }

    public void displayReceipt(Order order, OrderResponse details) {
        OutputUtil.printHeader("ORDER RECEIPT");
        OutputUtil.printSuccess("Your order has been successfully placed!");

        OutputUtil.println("Order ID     : " + ColorUtil.CYAN + order.getId() + ColorUtil.RESET);
        OutputUtil.println("Date         : " + (order.getCreatedAt() != null ? order.getCreatedAt().toString().substring(0, 19) : "-"));
        OutputUtil.println("Status       : " + ColorUtil.YELLOW + order.getStatus().name() + ColorUtil.RESET);
        OutputUtil.println("Total Amount : " + ColorUtil.BOLD + "$" + String.format("%.2f", order.getTotalAmount()) + ColorUtil.RESET);

        if (details != null && details.items() != null && !details.items().isEmpty()) {
            OutputUtil.printSubHeader("ITEMS PURCHASED");
            Table table = TableUtil.createTable(7, "#", "PRODUCT NAME", "SIZE", "COLOR", "QTY", "PRICE ($)", "SUBTOTAL ($)");
            List<OrderItemResponse> items = details.items();
            for (int i = 0; i < items.size(); i++) {
                OrderItemResponse item = items.get(i);
                table.addCell(String.valueOf(i + 1));
                table.addCell(item.productName() != null ? item.productName() : "Product");
                table.addCell(item.size() != null ? item.size().toString() : "-");
                table.addCell(item.color() != null ? item.color() : "-");
                table.addCell(String.valueOf(item.quantity()));
                table.addCell(item.unitPrice() != null ? String.format("%.2f", item.unitPrice()) : "0.00");
                table.addCell(item.subtotal() != null ? String.format("%.2f", item.subtotal()) : "0.00");
            }
            TableUtil.render(table);
        }
    }
}

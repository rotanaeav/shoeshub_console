package kh.com.shoeshub.features.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private UUID customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private boolean deleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Optional list of order items for eager view
    private List<OrderItem> items;
}

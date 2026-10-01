package kh.com.shoeshub.features.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private UUID id;
    private UUID orderId;
    private UUID variantId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private boolean deleted;
}

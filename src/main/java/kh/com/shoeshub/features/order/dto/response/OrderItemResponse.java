package kh.com.shoeshub.features.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID variantId,
        String productName,
        BigDecimal size,
        String color,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
    public OrderItemResponse(UUID id, UUID variantId, int quantity, BigDecimal unitPrice) {
        this(id, variantId, "Product", BigDecimal.ZERO, "-", quantity, unitPrice,
                unitPrice != null ? unitPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO);
    }
}

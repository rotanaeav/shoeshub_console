package kh.com.shoeshub.features.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutLine(
        UUID cartItemId,
        UUID variantId,
        String productName,
        BigDecimal size,
        String color,
        int quantity,
        int stock,
        BigDecimal price
) {
}

package kh.com.shoeshub.features.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID variantId,
        int quantity,
        BigDecimal unitPrice
) {
}

package kh.com.shoeshub.features.order.dto.request;

import java.util.UUID;

public record OrderRequest(
        UUID customerId
) {
}

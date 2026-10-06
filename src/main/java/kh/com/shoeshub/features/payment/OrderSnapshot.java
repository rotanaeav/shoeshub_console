package kh.com.shoeshub.features.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/* Small read-only copy of 4 order fields so Payment does not depend on the Order feature. Layer: DTO. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSnapshot {
    private UUID id;
    private UUID customerId;
    private String status;
    private BigDecimal totalAmount;
}

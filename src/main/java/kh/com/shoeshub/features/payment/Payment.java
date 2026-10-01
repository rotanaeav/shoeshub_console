package kh.com.shoeshub.features.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private UUID id;
    private UUID orderId;
    private PaymentMethod method;
    private BigDecimal amount;
    private PaymentStatus status;
    private boolean deleted;
    private OffsetDateTime createdAt;
}

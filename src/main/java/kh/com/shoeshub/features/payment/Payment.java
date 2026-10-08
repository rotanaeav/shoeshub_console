package kh.com.shoeshub.features.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/* Database entity for the payments table. Layer: Entity. */
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
    private Timestamp createdAt;
}

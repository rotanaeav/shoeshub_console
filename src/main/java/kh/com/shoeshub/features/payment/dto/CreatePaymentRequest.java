package kh.com.shoeshub.features.payment.dto;

import kh.com.shoeshub.features.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/* Collects payment input from the user. Layer: DTO. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private UUID orderId;
    // No amount field on purpose, the amount is read from the order.
    private PaymentMethod method;
}

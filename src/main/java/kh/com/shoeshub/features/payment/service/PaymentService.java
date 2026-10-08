package kh.com.shoeshub.features.payment.service;

import kh.com.shoeshub.features.payment.Payment;
import kh.com.shoeshub.features.payment.PaymentStatus;
import kh.com.shoeshub.features.payment.dto.CreatePaymentRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* Payment business logic. Layer: Service. */
public interface PaymentService {

    /* Processes a payment in a single transaction. */
    Payment processPayment(CreatePaymentRequest request, UUID customerId, boolean confirmed);

    /* Looks up a payment by order ID. */
    Optional<Payment> getPaymentByOrderId(UUID orderId);

    /* Returns the payment history for a customer. */
    List<Payment> getTransactionHistory(UUID customerId);

    /* Returns all payments for admin view. */
    List<Payment> getAllPayments();

    /* Filters payments by status. */
    List<Payment> getPaymentsByStatus(PaymentStatus status);
}

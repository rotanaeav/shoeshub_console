package kh.com.shoeshub.features.payment.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.payment.OrderSnapshot;
import kh.com.shoeshub.features.payment.Payment;
import kh.com.shoeshub.features.payment.PaymentStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Data access for payments. Layer: Repository. 
// Methods with a Connection parameter use the service's connection, so everything runs in ONE transaction.
public interface PaymentRepository extends CrudRepository<Payment, UUID> {

    // Finds a payment by its order ID.
    Optional<Payment> findByOrderId(UUID orderId);

    // Finds all payments for a specific customer.
    List<Payment> findAllByCustomerId(UUID customerId);

    // Finds all payments with a specific status.
    List<Payment> findAllByStatus(PaymentStatus status);

    // Updates the status of an existing payment.
    void updateStatus(UUID paymentId, PaymentStatus status);

    // Finds a payment by order ID using an existing transaction.
    Optional<Payment> findByOrderId(Connection conn, UUID orderId);

    // Inserts a new payment using an existing transaction.
    Payment save(Connection conn, Payment payment);

    // Updates a payment using an existing transaction.
    Payment update(Connection conn, UUID id, Payment payment);

    // Locks an order for payment processing.
    Optional<OrderSnapshot> lockOrderForPayment(Connection conn, UUID orderId);

    // Marks an order as PAID using an existing transaction.
    void markOrderPaid(Connection conn, UUID orderId);
}

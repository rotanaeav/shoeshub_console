package kh.com.shoeshub.features.payment.service;

import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.payment.OrderSnapshot;
import kh.com.shoeshub.features.payment.Payment;
import kh.com.shoeshub.features.payment.PaymentStatus;
import kh.com.shoeshub.features.payment.dto.CreatePaymentRequest;
import kh.com.shoeshub.features.payment.repository.PaymentRepository;
import kh.com.shoeshub.features.payment.repository.PaymentRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementation of PaymentService. Layer: Service. */
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository = new PaymentRepositoryImpl();

    /** Processes a payment within a single transaction. */
    @Override
    public Payment processPayment(CreatePaymentRequest request, UUID customerId, boolean confirmed) {
        // validate input
        if (request == null)
            throw new ValidationException("Payment request cannot be null");
        if (request.getOrderId() == null)
            throw new ValidationException("Order ID cannot be null");
        if (request.getMethod() == null)
            throw new ValidationException("Payment method cannot be null");
        if (customerId == null)
            throw new ValidationException("Customer ID cannot be null");

        try (Connection conn = DBConfig.get()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            // start transaction (setAutoCommit(false))
            conn.setAutoCommit(false);
            try {
                // lock the order row (FOR UPDATE) so two payments can't run together
                OrderSnapshot order = paymentRepository.lockOrderForPayment(conn, request.getOrderId())
                        .orElseThrow(() -> new NotFoundException("Order not found or access denied"));

                // check the owner (same "not found" message so other customers' orders stay
                // hidden)
                if (!order.getCustomerId().equals(customerId)) {
                    throw new NotFoundException("Order not found or access denied");
                }

                // only PENDING orders can be paid
                if (!"PENDING".equals(order.getStatus())) {
                    throw new ValidationException("Cannot process payment. Order status is: " + order.getStatus());
                }

                // amount comes from the order, never from the user
                PaymentStatus finalStatus = confirmed ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

                Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(conn, request.getOrderId());
                Payment resultPayment;

                if (existingPaymentOpt.isPresent()) {
                    Payment existing = existingPaymentOpt.get();

                    if (existing.getStatus() == PaymentStatus.SUCCESS) {
                        throw new ValidationException("Order is already paid.");
                    }

                    // retry UPDATES the same row because order_id is UNIQUE
                    existing.setMethod(request.getMethod());
                    existing.setAmount(order.getTotalAmount());
                    existing.setStatus(finalStatus);
                    resultPayment = paymentRepository.update(conn, existing.getId(), existing);
                } else {
                    Payment newPayment = Payment.builder()
                            .orderId(request.getOrderId())
                            .method(request.getMethod())
                            .amount(order.getTotalAmount())
                            .status(finalStatus)
                            .build();
                    resultPayment = paymentRepository.save(conn, newPayment);
                }

                // failed payment leaves the order PENDING
                if (confirmed) {
                    paymentRepository.markOrderPaid(conn, request.getOrderId());
                }

                // commit saves payment and order together
                conn.commit();
                return resultPayment;

            } catch (Exception e) {
                // rollback undoes everything on error
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new AppException("Failed to rollback transaction: " + rollbackEx.getMessage(), e);
                }
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new AppException("Database error during payment processing", e);
            } finally {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    // Ignore or log setting auto-commit back failure
                }
            }
        } catch (SQLException e) {
            throw new AppException("Failed to acquire database connection: " + e.getMessage(), e);
        }
    }

    /** Looks up a payment by order ID. */
    @Override
    public Optional<Payment> getPaymentByOrderId(UUID orderId) {
        if (orderId == null)
            throw new ValidationException("Order ID cannot be null");
        return paymentRepository.findByOrderId(orderId);
    }

    /** Gets payment history for a customer. */
    @Override
    public List<Payment> getTransactionHistory(UUID customerId) {
        if (customerId == null)
            throw new ValidationException("Customer ID cannot be null");
        return paymentRepository.findAllByCustomerId(customerId);
    }

    /** Gets all payments. */
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /** Filters payments by status. */
    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        if (status == null)
            throw new ValidationException("Status cannot be null");
        return paymentRepository.findAllByStatus(status);
    }
}

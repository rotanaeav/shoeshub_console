package kh.com.shoeshub.features.payment.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.AppException;
import kh.com.shoeshub.features.payment.OrderSnapshot;
import kh.com.shoeshub.features.payment.Payment;
import kh.com.shoeshub.features.payment.PaymentStatus;
import kh.com.shoeshub.features.payment.mapper.PaymentMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* JDBC implementation of PaymentRepository. Layer: Repository. */
// PreparedStatement "?" stops SQL injection; ?::payment_status is the enum
// cast; is_deleted = false is soft delete.
public class PaymentRepositoryImpl implements PaymentRepository {

    private final RowMapper<Payment> paymentMapper = new PaymentMapper();

    /* Saves a new payment to the database. */
    @Override
    public Payment save(Payment payment) {
        try (Connection conn = DBConfig.get()) {
            return save(conn, payment);
        } catch (SQLException e) {
            throw new AppException("Failed to save payment: " + e.getMessage(), e);
        }
    }

    /* Finds a payment by its ID. */
    @Override
    public Optional<Payment> findById(UUID id) {
        String sql = "SELECT * FROM payments WHERE id = ? AND is_deleted = false";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(paymentMapper.mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new AppException("Failed to query payment by id: " + e.getMessage(), e);
        }
    }

    /* Finds all payments. */
    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments WHERE is_deleted = false ORDER BY created_at DESC";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            return paymentMapper.mapRows(rs);
        } catch (SQLException e) {
            throw new AppException("Failed to query payments: " + e.getMessage(), e);
        }
    }

    /* Updates an existing payment. */
    @Override
    public Payment update(UUID id, Payment payment) {
        try (Connection conn = DBConfig.get()) {
            return update(conn, id, payment);
        } catch (SQLException e) {
            throw new AppException("Failed to update payment: " + e.getMessage(), e);
        }
    }

    /* Soft-deletes a payment by ID. */
    @Override
    public void deleteById(UUID id) {
        String sql = "UPDATE payments SET is_deleted = true WHERE id = ?";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Failed to delete payment: " + e.getMessage(), e);
        }
    }

    /* Finds a payment by its order ID. */
    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        try (Connection conn = DBConfig.get()) {
            return findByOrderId(conn, orderId);
        } catch (SQLException e) {
            throw new AppException("Failed to query payment by order id: " + e.getMessage(), e);
        }
    }

    /* Finds all payments for a given customer. */
    @Override
    public List<Payment> findAllByCustomerId(UUID customerId) {
        // The JOIN in findAllByCustomerId links payments to orders to check the
        // customer ID.
        String sql = "SELECT p.* FROM payments p " +
                "JOIN orders o ON p.order_id = o.id " +
                "WHERE o.customer_id = ? AND p.is_deleted = false " +
                "ORDER BY p.created_at DESC";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return paymentMapper.mapRows(rs);
            }
        } catch (SQLException e) {
            throw new AppException("Failed to query payments by customer: " + e.getMessage(), e);
        }
    }

    /* Finds all payments matching a status. */
    @Override
    public List<Payment> findAllByStatus(PaymentStatus status) {
        String sql = "SELECT * FROM payments WHERE status = ?::payment_status AND is_deleted = false ORDER BY created_at DESC";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                return paymentMapper.mapRows(rs);
            }
        } catch (SQLException e) {
            throw new AppException("Failed to query payments by status: " + e.getMessage(), e);
        }
    }

    /* Updates a payment's status. */
    @Override
    public void updateStatus(UUID paymentId, PaymentStatus status) {
        String sql = "UPDATE payments SET status = ?::payment_status WHERE id = ? AND is_deleted = false";
        try (Connection conn = DBConfig.get();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setObject(2, paymentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Failed to update payment status: " + e.getMessage(), e);
        }
    }

    /* Finds a payment by order ID in an existing transaction. */
    @Override
    public Optional<Payment> findByOrderId(Connection conn, UUID orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ? AND is_deleted = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(paymentMapper.mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new AppException("Failed to query payment by order id in transaction: " + e.getMessage(), e);
        }
    }

    /* Saves a new payment in an existing transaction. */
    @Override
    public Payment save(Connection conn, Payment payment) {
        // RETURNING * gets the newly generated row data like id and created_at.
        String sql = "INSERT INTO payments (order_id, method, amount, status) " +
                "VALUES (?, ?::payment_method, ?, ?::payment_status) RETURNING *";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, payment.getOrderId());
            stmt.setString(2, payment.getMethod().name());
            stmt.setBigDecimal(3, payment.getAmount());
            stmt.setString(4, payment.getStatus().name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return paymentMapper.mapRow(rs);
                }
                throw new AppException("Failed to insert payment, no result returned.");
            }
        } catch (SQLException e) {
            throw new AppException("Failed to insert payment: " + e.getMessage(), e);
        }
    }

    /* Updates a payment in an existing transaction. */
    @Override
    public Payment update(Connection conn, UUID id, Payment payment) {
        String sql = "UPDATE payments SET method = ?::payment_method, amount = ?, " +
                "status = ?::payment_status, created_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND is_deleted = false RETURNING *";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getMethod().name());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getStatus().name());
            stmt.setObject(4, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return paymentMapper.mapRow(rs);
                }
                throw new AppException("Failed to update payment, row not found.");
            }
        } catch (SQLException e) {
            throw new AppException("Failed to update payment: " + e.getMessage(), e);
        }
    }

    /* Locks an order for payment. */
    @Override
    public Optional<OrderSnapshot> lockOrderForPayment(Connection conn, UUID orderId) {
        // FOR UPDATE in lockOrderForPayment stops other transactions from reading the
        // row.
        String sql = "SELECT id, customer_id, status, total_amount FROM orders " +
                "WHERE id = ? AND is_deleted = false FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(OrderSnapshot.builder()
                            .id(rs.getObject("id", UUID.class))
                            .customerId(rs.getObject("customer_id", UUID.class))
                            .status(rs.getString("status"))
                            .totalAmount(rs.getBigDecimal("total_amount"))
                            .build());
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new AppException("Failed to lock order: " + e.getMessage(), e);
        }
    }

    /* Marks an order as PAID in an existing transaction. */
    @Override
    public void markOrderPaid(Connection conn, UUID orderId) {
        String sql = "UPDATE orders SET status = 'PAID'::order_status, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND is_deleted = false";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, orderId);
            // If no row was updated, the order isn't found, so throw to rollback.
            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new AppException("Failed to mark order as PAID: order not found");
            }
        } catch (SQLException e) {
            throw new AppException("Failed to mark order as PAID: " + e.getMessage(), e);
        }
    }
}

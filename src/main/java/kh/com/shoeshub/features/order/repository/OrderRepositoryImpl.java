package kh.com.shoeshub.features.order.repository;

import kh.com.shoeshub.common.RowMapper;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderItem;
import kh.com.shoeshub.features.order.OrderStatus;
import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.mapper.OrderItemRowMapper;
import kh.com.shoeshub.features.order.mapper.OrderRowMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository {

    private final RowMapper<Order> orderRowMapper = new OrderRowMapper();
    private final RowMapper<OrderItem> orderItemRowMapper = new OrderItemRowMapper();

    @Override
    public Order save(Order order) {
        try (Connection conn = DBConfig.getInstance().getConnection()) {
            return save(order, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order: " + e.getMessage(), e);
        }
    }

    @Override
    public Order save(Order order, Connection conn) {
        String sql;
        boolean hasCustomId = order.getId() != null;

        if (hasCustomId) {
            sql = """
                    INSERT INTO orders (
                        id,
                        customer_id,
                        status,
                        total_amount
                    )
                    VALUES (?, ?, ?::order_status, ?)
                    RETURNING *
                    """;
        } else {
            sql = """
                    INSERT INTO orders (
                        customer_id,
                        status,
                        total_amount
                    )
                    VALUES (?, ?::order_status, ?)
                    RETURNING *
                    """;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            OrderStatus status = order.getStatus() != null ? order.getStatus() : OrderStatus.PENDING;
            BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;

            if (hasCustomId) {
                ps.setObject(1, order.getId());
                ps.setObject(2, order.getCustomerId());
                ps.setString(3, status.name());
                ps.setBigDecimal(4, totalAmount);
            } else {
                ps.setObject(1, order.getCustomerId());
                ps.setString(2, status.name());
                ps.setBigDecimal(3, totalAmount);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order saved = orderRowMapper.mapRow(rs);
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        List<OrderItem> savedItems = new ArrayList<>();
                        for (OrderItem item : order.getItems()) {
                            item.setOrderId(saved.getId());
                            savedItems.add(saveOrderItem(item, conn));
                        }
                        saved.setItems(savedItems);
                    }
                    return saved;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute save order: " + e.getMessage(), e);
        }
    }

    @Override
    public OrderItem saveOrderItem(OrderItem item, Connection conn) {
        String sql = """
                INSERT INTO order_items (
                    order_id,
                    variant_id,
                    quantity,
                    unit_price
                )
                VALUES (?, ?, ?, ?)
                RETURNING *
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, item.getOrderId());
            ps.setObject(2, item.getVariantId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return orderItemRowMapper.mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order item: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {
        String sql = """
                SELECT * FROM orders
                WHERE id = ? AND is_deleted = FALSE
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = orderRowMapper.mapRow(rs);
                    order.setItems(findOrderItemsByOrderId(order.getId()));
                    return Optional.of(order);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = """
                SELECT * FROM orders
                WHERE is_deleted = FALSE
                ORDER BY created_at DESC
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order order = orderRowMapper.mapRow(rs);
                order.setItems(findOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all orders: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findAllByStatus(OrderStatus status) {
        String sql = """
                SELECT * FROM orders
                WHERE status = ?::order_status AND is_deleted = FALSE
                ORDER BY created_at DESC
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order order = orderRowMapper.mapRow(rs);
                    order.setItems(findOrderItemsByOrderId(order.getId()));
                    orders.add(order);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders by status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        String sql = """
                SELECT * FROM orders
                WHERE customer_id = ? AND is_deleted = FALSE
                ORDER BY created_at DESC
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order order = orderRowMapper.mapRow(rs);
                    order.setItems(findOrderItemsByOrderId(order.getId()));
                    orders.add(order);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders by customer ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Order update(UUID id, Order entity) {
        String sql = """
                UPDATE orders
                SET status = ?::order_status,
                    total_amount = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND is_deleted = FALSE
                RETURNING *
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, entity.getStatus().name());
            ps.setBigDecimal(2, entity.getTotalAmount());
            ps.setObject(3, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order updated = orderRowMapper.mapRow(rs);
                    updated.setItems(findOrderItemsByOrderId(id));
                    return updated;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(UUID id, OrderStatus status) {
        try (Connection conn = DBConfig.getInstance().getConnection()) {
            return updateStatus(id, status, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(UUID id, OrderStatus status, Connection conn) {
        String sql = """
                UPDATE orders
                SET status = ?::order_status,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND is_deleted = FALSE
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setObject(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update order status: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        String sql = """
                UPDATE orders
                SET is_deleted = TRUE,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to soft-delete order: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(UUID orderId) {
        String sql = """
                SELECT * FROM order_items
                WHERE order_id = ? AND is_deleted = FALSE
                ORDER BY id ASC
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(orderItemRowMapper.mapRow(rs));
                }
                return items;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order items: " + e.getMessage(), e);
        }
    }

    @Override
    public List<OrderItemResponse> findOrderItemsWithDetails(UUID orderId) {
        String sql = """
                SELECT 
                    oi.id,
                    oi.variant_id,
                    p.name AS product_name,
                    pv.size,
                    pv.color,
                    oi.quantity,
                    oi.unit_price,
                    (oi.quantity * oi.unit_price) AS subtotal
                FROM order_items oi
                JOIN product_variants pv ON oi.variant_id = pv.id
                JOIN products p ON pv.product_id = p.id
                WHERE oi.order_id = ? AND oi.is_deleted = FALSE
                ORDER BY oi.id ASC
                """;

        try (
                Connection conn = DBConfig.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setObject(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItemResponse> responses = new ArrayList<>();
                while (rs.next()) {
                    OrderItemResponse item = new OrderItemResponse(
                            rs.getObject("id", UUID.class),
                            rs.getObject("variant_id", UUID.class),
                            rs.getString("product_name"),
                            rs.getBigDecimal("size"),
                            rs.getString("color"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getBigDecimal("subtotal")
                    );
                    responses.add(item);
                }
                return responses;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find detailed order items: " + e.getMessage(), e);
        }
    }
}
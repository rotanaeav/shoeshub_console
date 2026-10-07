package kh.com.shoeshub.features.order.service;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.config.DBConfig;
import kh.com.shoeshub.exception.BusinessException;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.cart.dto.CartItemResponse;
import kh.com.shoeshub.features.cart.repository.CartRepository;
import kh.com.shoeshub.features.cart.repository.CartRepositoryImpl;
import kh.com.shoeshub.features.order.Order;
import kh.com.shoeshub.features.order.OrderItem;
import kh.com.shoeshub.features.order.OrderStatus;
import kh.com.shoeshub.features.order.dto.response.OrderItemResponse;
import kh.com.shoeshub.features.order.dto.response.OrderResponse;
import kh.com.shoeshub.features.order.repository.OrderRepository;
import kh.com.shoeshub.features.order.repository.OrderRepositoryImpl;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;
import kh.com.shoeshub.features.product.repository.ProductVariantRepositoryImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final AuthorizationService authorizationService;

    public OrderServiceImpl() {
        this.orderRepository = new OrderRepositoryImpl();
        this.cartRepository = new CartRepositoryImpl();
        this.variantRepository = new ProductVariantRepositoryImpl();
        this.authorizationService = null;
    }

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductVariantRepository variantRepository,
            AuthorizationService authorizationService
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.variantRepository = variantRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Order checkout(UUID customerId) {
        if (customerId == null) {
            throw new ValidationException("Customer ID is required for checkout.");
        }

        List<CartItemResponse> cartItems = cartRepository.findByUserId(customerId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ValidationException("Your cart is empty. Please add items before checking out.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemResponse item : cartItems) {
            if (item.getQuantity() <= 0) {
                throw new ValidationException("Invalid quantity for " + item.getProductName());
            }
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        try (Connection conn = DBConfig.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                Order order = Order.builder()
                        .customerId(customerId)
                        .status(OrderStatus.PENDING)
                        .totalAmount(totalAmount)
                        .build();

                Order savedOrder = orderRepository.save(order, conn);
                if (savedOrder == null) {
                    throw new SQLException("Failed to create order record.");
                }

                List<OrderItem> savedItems = new ArrayList<>();
                for (CartItemResponse item : cartItems) {
                    boolean stockDeducted = variantRepository.updateStockWithConnection(
                            conn,
                            item.getVariantId(),
                            item.getQuantity()
                    );
                    if (!stockDeducted) {
                        throw new ValidationException("Insufficient stock for item: "
                                + item.getProductName() + " (Size: " + item.getSize() + ", Color: " + item.getColor() + ")");
                    }

                    OrderItem orderItem = OrderItem.builder()
                            .orderId(savedOrder.getId())
                            .variantId(item.getVariantId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getPrice())
                            .build();

                    OrderItem savedItem = orderRepository.saveOrderItem(orderItem, conn);
                    savedItems.add(savedItem);
                }
                savedOrder.setItems(savedItems);

                String clearCartSql = """
                        UPDATE cart_items
                        SET is_deleted = TRUE
                        WHERE user_id = ? AND is_deleted = FALSE
                        """;
                try (PreparedStatement psClear = conn.prepareStatement(clearCartSql)) {
                    psClear.setObject(1, customerId);
                    psClear.executeUpdate();
                }

                conn.commit();
                return savedOrder;

            } catch (Exception e) {
                conn.rollback();
                if (e instanceof ValidationException ve) {
                    throw ve;
                }
                throw new RuntimeException("Transaction rolled back: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during checkout: " + e.getMessage(), e);
        }
    }

    @Override
    public Order placeOrder(Order order) {
        if (order == null || order.getCustomerId() == null) {
            throw new ValidationException("Order and customer ID are required.");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("An order needs at least one item.");
        }

        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getMyOrders(UUID customerId) {
        if (customerId == null) {
            throw new ValidationException("Customer ID is required.");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public OrderResponse getOrderDetail(UUID orderId, UUID requesterId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && requesterId != null && !order.getCustomerId().equals(requesterId)) {
            throw new BusinessException("You are not authorized to view this order.");
        }

        List<OrderItemResponse> itemResponses = orderRepository.findOrderItemsWithDetails(orderId);

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }

    @Override
    public void cancelOrder(UUID orderId, UUID customerId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (!isAdmin && !order.getCustomerId().equals(customerId)) {
            throw new BusinessException("You are not authorized to cancel this order.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be cancelled. Current status is " + order.getStatus() + ".");
        }

        try (Connection conn = DBConfig.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean updated = orderRepository.updateStatus(orderId, OrderStatus.CANCELLED, conn);
                if (!updated) {
                    throw new SQLException("Failed to update order status to CANCELLED.");
                }

                List<OrderItem> items = orderRepository.findOrderItemsByOrderId(orderId);
                String restoreStockSql = """
                        UPDATE product_variants
                        SET stock_quantity = stock_quantity + ?
                        WHERE id = ? AND is_deleted = FALSE
                        """;
                try (PreparedStatement psRestore = conn.prepareStatement(restoreStockSql)) {
                    for (OrderItem item : items) {
                        psRestore.setInt(1, item.getQuantity());
                        psRestore.setObject(2, item.getVariantId());
                        psRestore.addBatch();
                    }
                    psRestore.executeBatch();
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Failed to cancel order: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during order cancellation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Order> getAllOrders(OrderStatus statusFilter) {
        if (statusFilter == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findAllByStatus(statusFilter);
    }

    @Override
    public void updateStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == newStatus) {
            return;
        }

        if (!order.getStatus().canChangeTo(newStatus)) {
            throw new BusinessException("Cannot transition order status from " + order.getStatus() + " to " + newStatus + ".");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            cancelOrder(orderId, order.getCustomerId(), true);
            return;
        }

        boolean updated = orderRepository.updateStatus(orderId, newStatus);
        if (!updated) {
            throw new RuntimeException("Failed to update status for order: " + orderId);
        }
    }
}

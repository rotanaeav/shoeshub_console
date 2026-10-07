package kh.com.shoeshub.features.order;

public enum OrderStatus {
    PENDING, PAID, SHIPPED, DELIVERED, CANCELLED;

    public boolean canChangeTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PAID;
            case PAID -> next == SHIPPED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}

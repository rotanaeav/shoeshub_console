package kh.com.shoeshub.features.order;

public enum OrderStatus {
    PENDING,
    PAID,
    CANCELLED;

    public boolean canChangeTo(OrderStatus next) {
        if (next == null) {
            return false;
        }
        return switch (this) {
            case PENDING -> next == PAID || next == CANCELLED;
            case PAID, CANCELLED -> false;
        };
    }
}

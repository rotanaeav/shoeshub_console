package kh.com.shoeshub.features.payment;

/* Enum names must match the PostgreSQL payment_status enum. Layer: Entity. */
public enum PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}

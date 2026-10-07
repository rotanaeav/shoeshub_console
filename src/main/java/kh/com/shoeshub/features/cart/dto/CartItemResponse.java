package kh.com.shoeshub.features.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemResponse {

    private UUID cartItemId;
    private UUID variantId;

    private String productName;
    private BigDecimal size;
    private String color;

    private BigDecimal price;
    private int quantity;

    public CartItemResponse(
            UUID cartItemId,
            UUID variantId,
            String productName,
            BigDecimal size,
            String color,
            BigDecimal price,
            int quantity
    ) {
        this.cartItemId = cartItemId;
        this.variantId = variantId;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    public UUID getCartItemId() {
        return cartItemId;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
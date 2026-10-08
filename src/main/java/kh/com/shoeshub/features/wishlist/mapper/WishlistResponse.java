package kh.com.shoeshub.features.wishlist.mapper;

import java.math.BigDecimal;
import java.util.UUID;

public class WishlistResponse {

    private UUID productId;
    private String productName;
    private String sku;
    private String description;
    private BigDecimal price;

    public WishlistResponse(
            UUID productId,
            String productName,
            String sku,
            String description,
            BigDecimal price
    ) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.description = description;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

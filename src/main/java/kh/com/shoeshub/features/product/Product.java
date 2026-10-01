package kh.com.shoeshub.features.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private Short categoryId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
    private boolean deleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

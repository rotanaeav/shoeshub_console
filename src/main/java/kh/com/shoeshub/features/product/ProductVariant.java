package kh.com.shoeshub.features.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    private UUID id;
    private UUID productId;
    private BigDecimal size;
    private String color;
    private Integer stockQuantity;
    private boolean deleted;
}

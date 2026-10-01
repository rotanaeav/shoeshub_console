package kh.com.shoeshub.features.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private Short categoryId;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
}

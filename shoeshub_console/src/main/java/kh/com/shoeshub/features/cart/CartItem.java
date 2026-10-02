package kh.com.shoeshub.features.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private UUID id;
    private UUID userId;
    private UUID variantId;
    private Integer quantity;
    private boolean deleted;
}

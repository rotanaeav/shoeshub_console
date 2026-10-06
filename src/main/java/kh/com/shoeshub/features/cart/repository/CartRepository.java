package kh.com.shoeshub.features.cart.repository;

import kh.com.shoeshub.features.cart.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository{

        CartItem save(CartItem cartItem);

        Optional<CartItem> findById(UUID id);

    Optional<CartItem> findByUserIdAndVariantIdIncludeDeleted(
            UUID userId,
            UUID variantId
    );

    CartItem restore(UUID id, int quantity);

        Optional<CartItem> findByUserIdAndVariantId(UUID userId, UUID variantId);

        List<CartItem> findByUserId(UUID userId);

        CartItem updateQuantity(UUID id, int quantity);

        boolean deleteById(UUID id);

        boolean clearCart(UUID userId);
}

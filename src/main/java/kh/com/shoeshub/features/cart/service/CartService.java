package kh.com.shoeshub.features.cart.service;

import kh.com.shoeshub.features.cart.CartItem;

import java.util.List;
import java.util.UUID;

public interface CartService {

    CartItem addToCart(
            UUID userId,
            UUID variantId,
            int quantity
    );

    List<CartItem> getMyCart(UUID userId);

    CartItem updateQuantity(
            UUID userId,
            UUID cartItemId,
            int quantity
    );

    void removeFromCart(
            UUID userId,
            UUID cartItemId
    );

    void clearCart(UUID userId);
}
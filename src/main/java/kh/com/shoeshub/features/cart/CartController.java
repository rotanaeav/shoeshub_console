package kh.com.shoeshub.features.cart;

import kh.com.shoeshub.features.cart.service.CartService;
import kh.com.shoeshub.features.cart.service.CartServiceImpl;

import java.util.List;
import java.util.UUID;

public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

   public CartItem addToCart(
            UUID userId,
            UUID variantId,
            int quantity
    ) {
        return cartService.addToCart(userId, variantId, quantity);
    }

    public List<CartItem> getMyCart(UUID userId){
        return cartService.getMyCart(userId);
    }

    public CartItem updateQuantity(
            UUID userId,
            UUID cartItemId,
            int quantity
    ){
        return cartService.updateQuantity(userId, cartItemId,quantity);
    }

    public  void removeFromCart(
            UUID userId,
            UUID cartItemId
    ) {
        cartService.removeFromCart(userId, cartItemId);
    }

    public void clearCart(UUID userId) {
        cartService.clearCart(userId);
    }

    public void handleViewCart(UUID userId) {
        CartUI cartUI = new CartUI(this);
        cartUI.showCartMenu(userId);
    }
}

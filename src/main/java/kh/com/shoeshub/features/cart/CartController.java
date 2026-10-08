package kh.com.shoeshub.features.cart;

import kh.com.shoeshub.features.cart.dto.CartItemResponse;
import kh.com.shoeshub.features.cart.service.CartService;
import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.service.ProductService;

import java.util.List;
import java.util.UUID;

public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    public CartController(CartService cartService) {
        this(cartService, null);
    }

   public CartItem addToCart(
            UUID userId,
            UUID variantId,
            int quantity
    ) {
        return cartService.addToCart(userId, variantId, quantity);
    }

    public List<CartItemResponse> getMyCart(UUID userId) {
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

    public List<Product> getActiveProducts() {
        return productService != null ? productService.getActiveProducts() : List.of();
    }

    public List<ProductVariant> getVariantsByProductId(UUID productId) {
        return productService != null ? productService.getVariantsByProductId(productId) : List.of();
    }
}

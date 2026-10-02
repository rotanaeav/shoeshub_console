package kh.com.shoeshub.features.cart;

import kh.com.shoeshub.features.cart.service.CartService;
import kh.com.shoeshub.features.cart.service.CartServiceImpl;

public class CartController {

    private final CartService cartService = new CartServiceImpl();
    private final CartUI cartUI = new CartUI();

    public void handleViewCart() {
        // TODO: Implement view cart action
    }
}

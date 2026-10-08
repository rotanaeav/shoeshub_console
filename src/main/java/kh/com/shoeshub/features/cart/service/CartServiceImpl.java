package kh.com.shoeshub.features.cart.service;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.exception.NotFoundException;
import kh.com.shoeshub.exception.ValidationException;
import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.cart.CartItem;
import kh.com.shoeshub.features.cart.dto.CartItemResponse;
import kh.com.shoeshub.features.cart.repository.CartRepository;
import kh.com.shoeshub.features.cart.repository.CartRepositoryImpl;
import kh.com.shoeshub.features.product.ProductVariant;
import kh.com.shoeshub.features.product.repository.ProductVariantRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final AuthorizationService authorizationService;

    public CartServiceImpl(
            CartRepository cartRepository,
            ProductVariantRepository variantRepository,
            AuthorizationService authorizationService
    ) {
        this.cartRepository = cartRepository;
        this.variantRepository = variantRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public CartItem addToCart(
            UUID userId,
            UUID variantId,
            int quantity
    ) {

        if (quantity <= 0) {
            throw new ValidationException(
                    "Quantity must be greater than zero."
            );
        }

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Variant not found: " + variantId
                        )
                );

        if (variant.getStockQuantity() < quantity) {
            throw new ValidationException(
                    "Not enough stock. Available: "
                            + variant.getStockQuantity()
            );
        }

        Optional<CartItem> existing =
                cartRepository.findByUserIdAndVariantIdIncludeDeleted(
                        userId,
                        variantId
                );

        if (existing.isPresent()) {

            CartItem cartItem = existing.get();

            if (cartItem.isDeleted()) {
                return cartRepository.restore(
                        cartItem.getId(),
                        quantity
                );
            }

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (newQuantity > variant.getStockQuantity()) {
                throw new ValidationException(
                        "Not enough stock. Available: "
                                + variant.getStockQuantity()
                );
            }

            return cartRepository.updateQuantity(
                    cartItem.getId(),
                    newQuantity
            );
        }

        CartItem cartItem = CartItem.builder()
                .userId(userId)
                .variantId(variantId)
                .quantity(quantity)
                .deleted(false)
                .build();

        return cartRepository.save(cartItem);
    }

    @Override
    public List<CartItemResponse> getMyCart(UUID userId) {

        AuthenticatedUser currentUser = authorizationService.requireAuthenticated();

        return cartRepository.findByUserId(currentUser.id());
    }

    @Override
    public CartItem updateQuantity(
            UUID userId,
            UUID cartItemId,
            int quantity
    ) {

        if (quantity <= 0) {
            throw new ValidationException(
                    "Quantity must be greater than zero."
            );
        }

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Cart item not found: " + cartItemId
                        )
                );

        if (!cartItem.getUserId().equals(userId)) {
            throw new ValidationException(
                    "You cannot update another user's cart item."
            );
        }

        ProductVariant variant =
                variantRepository.findById(cartItem.getVariantId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Variant not found: "
                                                + cartItem.getVariantId()
                                )
                        );

        if (quantity > variant.getStockQuantity()) {
            throw new ValidationException(
                    "Not enough stock. Available: "
                            + variant.getStockQuantity()
            );
        }

        return cartRepository.updateQuantity(
                cartItemId,
                quantity
        );
    }

    @Override
    public void removeFromCart(
            UUID userId,
            UUID cartItemId
    ) {

        CartItem cartItem = cartRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Cart item not found: " + cartItemId
                        )
                );

        if (!cartItem.getUserId().equals(userId)) {
            throw new ValidationException(
                    "You cannot remove another user's cart item."
            );
        }

        boolean deleted =
                cartRepository.deleteById(cartItemId);

        if (!deleted) {
            throw new NotFoundException(
                    "Cart item not found: " + cartItemId
            );
        }
    }

    @Override
    public void clearCart(UUID userId) {

        cartRepository.clearCart(userId);
    }
}
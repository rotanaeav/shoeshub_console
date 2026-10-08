package kh.com.shoeshub.features.wishlist;

import kh.com.shoeshub.features.product.Product;
import kh.com.shoeshub.features.product.service.ProductService;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.features.wishlist.service.WishlistService;

import java.util.List;
import java.util.UUID;

public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;

    public WishlistController(WishlistService wishlistService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
    }

    public WishlistController(WishlistService wishlistService) {
        this(wishlistService, null);
    }

    public Wishlist addToWishlist(UUID productId) {
        return wishlistService.addToWishlist(productId);
    }

    public List<WishlistResponse> getMyWishlist() {
        return wishlistService.getMyWishlist();
    }

    public boolean removeFromWishlist(UUID productId) {
        return wishlistService.removeFromWishlist(productId);
    }

    public Wishlist restoreWishlist(UUID productId) {
        return wishlistService.restoreWishlist(productId);
    }

    public List<Product> getActiveProducts() {
        return productService != null ? productService.getActiveProducts() : List.of();
    }
}
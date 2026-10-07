package kh.com.shoeshub.features.wishlist;

import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.features.wishlist.service.WishlistService;

import java.util.List;
import java.util.UUID;

public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
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
}
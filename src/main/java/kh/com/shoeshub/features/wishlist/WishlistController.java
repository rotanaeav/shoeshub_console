package kh.com.shoeshub.features.wishlist;

import kh.com.shoeshub.features.wishlist.service.WishlistService;
import kh.com.shoeshub.features.wishlist.service.WishlistServiceImpl;

public class WishlistController {

    private final WishlistService wishlistService = new WishlistServiceImpl();
    private final WishlistUI wishlistUI = new WishlistUI();

    public void handleViewWishlist() {
        // TODO: Implement view wishlist action
    }
}

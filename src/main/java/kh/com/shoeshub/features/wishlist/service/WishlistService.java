package kh.com.shoeshub.features.wishlist.service;

import kh.com.shoeshub.features.wishlist.Wishlist;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    Wishlist addToWishlist(UUID productId);

    List<WishlistResponse> getMyWishlist();

    boolean removeFromWishlist(UUID productId);

    Wishlist restoreWishlist(UUID productId);

    List<WishlistResponse> getMyWishlistDetails(UUID userId);
}

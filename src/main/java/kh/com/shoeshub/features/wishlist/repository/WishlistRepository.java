package kh.com.shoeshub.features.wishlist.repository;

import kh.com.shoeshub.features.wishlist.Wishlist;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository {

    Wishlist save(Wishlist wishlist);

    Optional<Wishlist> findByUserIdAndProductId(
            UUID userId,
            UUID productId
    );

    List<Wishlist> findByUserId(UUID userId);

    List<WishlistResponse> findWishlistDetailsByUserId(UUID userId);

    boolean deleteByUserIdAndProductId(
            UUID userId,
            UUID productId
    );

    Wishlist restore(
            UUID userId,
            UUID productId
    );
}
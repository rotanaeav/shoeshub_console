package kh.com.shoeshub.features.wishlist.service;

import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.features.auth.AuthenticatedUser;
import kh.com.shoeshub.features.wishlist.Wishlist;
import kh.com.shoeshub.features.wishlist.mapper.WishlistResponse;
import kh.com.shoeshub.features.wishlist.repository.WishlistRepository;
import kh.com.shoeshub.features.wishlist.repository.WishlistRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AuthorizationService authorizationService;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            AuthorizationService authorizationService
    ) {
        this.wishlistRepository = wishlistRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public Wishlist addToWishlist(UUID productId) {

        AuthenticatedUser currentUser =
                authorizationService.requireAuthenticated();

        UUID userId = currentUser.id();

        Optional<Wishlist> existing =
                wishlistRepository.findByUserIdAndProductId(
                        userId,
                        productId
                );

        if (existing.isPresent()) {

            Wishlist wishlist = existing.get();

            if (!wishlist.isDeleted()) {
                throw new IllegalArgumentException(
                        "Product is already in your wishlist."
                );
            }

            return wishlistRepository.restore(
                    userId,
                    productId
            );
        }

        Wishlist wishlist = new Wishlist(
                userId,
                productId,
                false
        );

        return wishlistRepository.save(wishlist);
    }

    @Override
    public List<WishlistResponse> getMyWishlist() {

        AuthenticatedUser currentUser =
                authorizationService.requireAuthenticated();

        return wishlistRepository.findWishlistDetailsByUserId(
                currentUser.id()
        );
    }

    @Override
    public List<WishlistResponse> getMyWishlistDetails(UUID userId) {
        return wishlistRepository.findWishlistDetailsByUserId(userId);
    }

    @Override
    public boolean removeFromWishlist(UUID productId) {

        AuthenticatedUser currentUser =
                authorizationService.requireAuthenticated();

        boolean deleted =
                wishlistRepository.deleteByUserIdAndProductId(
                        currentUser.id(),
                        productId
                );

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Product is not in your wishlist."
            );
        }

        return true;
    }

    @Override
    public Wishlist restoreWishlist(UUID productId) {

        AuthenticatedUser currentUser =
                authorizationService.requireAuthenticated();

        Wishlist wishlist =
                wishlistRepository.restore(
                        currentUser.id(),
                        productId
                );

        if (wishlist == null) {
            throw new IllegalArgumentException(
                    "Wishlist item not found or is already active."
            );
        }

        return wishlist;
    }
}

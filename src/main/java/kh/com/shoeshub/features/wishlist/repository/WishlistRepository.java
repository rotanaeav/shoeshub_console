package kh.com.shoeshub.features.wishlist.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.wishlist.Wishlist;

import java.util.UUID;

public interface WishlistRepository extends CrudRepository<Wishlist, UUID> {
    // TODO: Define custom wishlist queries (e.g. findByUserId, deleteByUserIdAndProductId)
}

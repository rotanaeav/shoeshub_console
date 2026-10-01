package kh.com.shoeshub.features.cart.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.cart.CartItem;

import java.util.UUID;

public interface CartRepository extends CrudRepository<CartItem, UUID> {
    // TODO: Define custom cart queries (e.g. findByUserId, clearUserCart)
}

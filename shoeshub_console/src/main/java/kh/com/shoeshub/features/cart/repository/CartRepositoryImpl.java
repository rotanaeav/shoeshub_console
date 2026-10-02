package kh.com.shoeshub.features.cart.repository;

import kh.com.shoeshub.features.cart.CartItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CartRepositoryImpl implements CartRepository {

    @Override
    public CartItem save(CartItem entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<CartItem> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<CartItem> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public CartItem update(UUID id, CartItem entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC delete
    }
}

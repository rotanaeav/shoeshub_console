package kh.com.shoeshub.features.wishlist.repository;

import kh.com.shoeshub.features.wishlist.Wishlist;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WishlistRepositoryImpl implements WishlistRepository {

    @Override
    public Wishlist save(Wishlist entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<Wishlist> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Wishlist> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public Wishlist update(UUID id, Wishlist entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }
}

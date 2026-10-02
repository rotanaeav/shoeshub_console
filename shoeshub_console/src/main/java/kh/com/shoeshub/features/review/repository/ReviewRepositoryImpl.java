package kh.com.shoeshub.features.review.repository;

import kh.com.shoeshub.features.review.Review;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReviewRepositoryImpl implements ReviewRepository {

    @Override
    public Review save(Review entity) {
        // TODO: Implement JDBC insert
        return entity;
    }

    @Override
    public Optional<Review> findById(UUID id) {
        // TODO: Implement JDBC query by id
        return Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        // TODO: Implement JDBC query all
        return Collections.emptyList();
    }

    @Override
    public Review update(UUID id, Review entity) {
        // TODO: Implement JDBC update
        return entity;
    }

    @Override
    public void deleteById(UUID id) {
        // TODO: Implement JDBC soft delete
    }
}

package kh.com.shoeshub.features.review.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.review.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends CrudRepository<Review, UUID> {
    List<Review> findByProductId(UUID productId);
    Optional<Review> findByUserAndProduct(UUID userId, UUID productId);
    double getAverageRating(UUID productId);
    boolean isActiveCustomer(UUID userId);
    boolean productExists(UUID productId);
    void deleteOwned(UUID reviewId, UUID userId);
}

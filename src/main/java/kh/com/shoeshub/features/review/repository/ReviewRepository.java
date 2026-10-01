package kh.com.shoeshub.features.review.repository;

import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.review.Review;

import java.util.UUID;

public interface ReviewRepository extends CrudRepository<Review, UUID> {
    // TODO: Define custom review queries (e.g. findByProductId, getAverageRating)
}

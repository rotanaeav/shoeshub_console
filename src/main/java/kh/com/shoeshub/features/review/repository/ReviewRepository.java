package kh.com.shoeshub.features.review.repository;

import java.util.*;
import kh.com.shoeshub.common.CrudRepository;
import kh.com.shoeshub.features.review.Review;

public interface ReviewRepository extends CrudRepository<Review, UUID> {
  List<Review> findByProductId(UUID productId);

  List<Review> findByUserId(UUID userId);

  double getAverageRating(UUID productId);
}

package kh.com.shoeshub.features.review.service;

import java.util.*;
import kh.com.shoeshub.features.review.Review;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;

public interface ReviewService {
  Review addReview(CreateReviewRequest request);

  Review updateReview(UUID id, short rating, String comment);

  void deleteReview(UUID id);

  List<Review> getProductReviews(UUID productId);

  double getAverageRating(UUID productId);

  List<Review> getMyReviews();

  List<Review> getAllReviews();
}

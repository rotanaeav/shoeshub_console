package kh.com.shoeshub.features.review.service;

import kh.com.shoeshub.features.review.Review;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review addReview(UUID currentUserId, CreateReviewRequest request);
    Review updateReview(UUID currentUserId, CreateReviewRequest request);
    void deleteReview(UUID currentUserId, UUID productId);
    List<Review> getReviewsByProduct(UUID productId);
    double getAverageRating(UUID productId);
}

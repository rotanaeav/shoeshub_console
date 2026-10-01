package kh.com.shoeshub.features.review;

import kh.com.shoeshub.features.review.service.ReviewService;
import kh.com.shoeshub.features.review.service.ReviewServiceImpl;

public class ReviewController {

    private final ReviewService reviewService = new ReviewServiceImpl();
    private final ReviewUI reviewUI = new ReviewUI();

    public void handleAddReview() {
        // TODO: Implement add review
    }
}

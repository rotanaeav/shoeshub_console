package kh.com.shoeshub.features.review;

import kh.com.shoeshub.features.review.service.ReviewService;
import kh.com.shoeshub.features.review.service.ReviewServiceImpl;

import java.util.UUID;

public class ReviewController {

    private final ReviewService reviewService = new ReviewServiceImpl();
    private final ReviewUI reviewUI = new ReviewUI();

    private UUID currentUserId;
    public ReviewController() {}
    public ReviewController(UUID userId) { setCurrentUser(userId); }
    // Supply this ID from the team's successful login, never from a customer input field.
    public void setCurrentUser(UUID userId) { currentUserId=userId; }

    public void handleAddReview() {
        while (true) {
            int choice=reviewUI.menu(); if (choice==0) return;
            try {
                switch (choice) {
                    case 1 -> { reviewService.addReview(currentUserId,reviewUI.readRequest()); OutputUtil.printSuccess("Review saved."); }
                    case 2 -> {
                        UUID product=reviewUI.productId();
                        reviewUI.show(reviewService.getReviewsByProduct(product),reviewService.getAverageRating(product));
                    }
                    case 3 -> { reviewService.updateReview(currentUserId,reviewUI.readRequest()); OutputUtil.printSuccess("Review updated."); }
                    case 4 -> {
                        UUID product=reviewUI.productId();
                        if (InputUtil.readConfirm("Delete your review?")) {
                            reviewService.deleteReview(currentUserId,product); OutputUtil.printSuccess("Review deleted.");
                        }
                    }
                }
            } catch (AppException e) { OutputUtil.printError(e.getMessage()); }
        }
    }
}

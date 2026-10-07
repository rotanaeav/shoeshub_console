package kh.com.shoeshub.features.review.dto;

import java.util.UUID;

public class CreateReviewRequest {
    private UUID productId;
    private int rating;
    private String comment;

    public CreateReviewRequest() {}

    public CreateReviewRequest(UUID productId, int rating, String comment) {
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public UUID getProductId() {
        return productId;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}

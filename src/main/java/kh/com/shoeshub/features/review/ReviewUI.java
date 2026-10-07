package kh.com.shoeshub.features.review;

import kh.com.shoeshub.features.review.dto.CreateReviewRequest;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReviewUI {

    public int menu() {
        OutputUtil.printHeader("REVIEWS & RATINGS");
        OutputUtil.println("[1] Add review   [2] View product reviews & average rating");
        OutputUtil.println("[3] Edit my review   [4] Delete my review   [0] Back");
        return InputUtil.readInt("Choose",0,4);
    }
    public UUID productId() { return InputUtil.readUUID("Product UUID (from your product catalog)"); }
    public CreateReviewRequest readRequest() {
        return new CreateReviewRequest(productId(),InputUtil.readInt("Rating (1-5)",1,5),
                InputUtil.readText("Comment (optional; max 2000 characters)"));
    }
    public void show(List<Review> reviews, double average) {
        OutputUtil.println(String.format(Locale.ROOT,"Average: %.2f / 5 | Reviews: %d",average,reviews.size()));
        if (reviews.isEmpty()) OutputUtil.printInfo("No reviews yet.");
        for (Review r:reviews) {
            OutputUtil.println("----------------------------------------");
            OutputUtil.println("Customer: "+r.getUserId()+" | Rating: "+r.getRating()+"/5 | "+r.getCreatedAt());
            OutputUtil.println(r.getComment()==null ? "" : r.getComment());
        }
    }

}

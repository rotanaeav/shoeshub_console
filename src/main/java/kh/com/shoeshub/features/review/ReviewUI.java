package kh.com.shoeshub.features.review;

import java.util.List;
import kh.com.shoeshub.utils.*;

public class ReviewUI {
  public void displayReviews(List<Review> reviews) {
    if (reviews.isEmpty()) {
      OutputUtil.printInfo("No reviews yet.");
      return;
    }
    var table =
        TableUtil.createTable(
            6, "Review ID", "Product ID", "Customer ID", "Rating", "Comment", "Created");
    reviews.forEach(
        r -> {
          table.addCell(r.getId().toString());
          table.addCell(r.getProductId().toString());
          table.addCell(r.getUserId().toString());
          table.addCell(r.getRating() + "/5");
          table.addCell(r.getComment() == null ? "" : r.getComment().replaceAll("[\\r\\n]", " "));
          table.addCell(r.getCreatedAt().toString());
        });
    TableUtil.render(table);
  }
}

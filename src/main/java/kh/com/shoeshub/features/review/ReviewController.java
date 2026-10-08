package kh.com.shoeshub.features.review;

import java.util.UUID;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;
import kh.com.shoeshub.features.review.service.ReviewService;
import kh.com.shoeshub.utils.*;

public class ReviewController {
  private final ReviewService service;
  private final ProductRepository products;
  private final ReviewUI ui = new ReviewUI();

  public ReviewController(ReviewService service, ProductRepository products) {
    this.service = service;
    this.products = products;
  }

  private void run(Runnable action) {
    try {
      action.run();
    } catch (RuntimeException e) {
      OutputUtil.printError(e.getMessage());
    }
  }

  private UUID chooseProduct() {
    var list = products.findAll().stream().filter(p -> p.isActive() && !p.isDeleted()).toList();
    if (list.isEmpty()) {
      OutputUtil.printInfo("No products available.");
      return null;
    }
    for (int i = 0; i < list.size(); i++)
      OutputUtil.println(
          " [" + (i + 1) + "] " + list.get(i).getSku() + " - " + list.get(i).getName());
    int n = InputUtil.readInt("Product (0 to back)", 0, list.size());
    return n == 0 ? null : list.get(n - 1).getId();
  }

  public void handleViewProductReviews() {
    run(
        () -> {
          var id = chooseProduct();
          if (id != null) showProductReviews(id);
        });
  }

  public void showProductReviews(UUID id) {
    run(
        () -> {
          ui.displayReviews(service.getProductReviews(id));
          OutputUtil.printInfo("Average rating: %.2f/5".formatted(service.getAverageRating(id)));
        });
  }

  public void handleAddReview() {
    run(
        () -> {
          var id = chooseProduct();
          if (id != null) addReview(id);
        });
  }

  public void addReview(UUID id) {
    run(
        () -> {
          service.addReview(
              new CreateReviewRequest(
                  id,
                  (short) InputUtil.readInt("Rating", 1, 5),
                  InputUtil.readText("Comment (optional)")));
          OutputUtil.printSuccess("Review saved.");
        });
  }

  public void showMenu() {
    while (true) {
      OutputUtil.printHeader("REVIEWS & RATINGS");
      OutputUtil.println(
          " [1] View product reviews\n"
              + " [2] Add review\n"
              + " [3] My reviews\n"
              + " [4] Edit my review\n"
              + " [5] Delete my review\n"
              + " [0] Back");
      switch (InputUtil.readInt("Choose menu", 0, 5)) {
        case 0 -> {
          return;
        }
        case 1 -> handleViewProductReviews();
        case 2 -> handleAddReview();
        case 3 -> run(() -> ui.displayReviews(service.getMyReviews()));
        case 4 ->
            run(
                () -> {
                  ui.displayReviews(service.getMyReviews());
                  var id = InputUtil.readUUID("Review ID");
                  service.updateReview(
                      id,
                      (short) InputUtil.readInt("New rating", 1, 5),
                      InputUtil.readText("New comment"));
                  OutputUtil.printSuccess("Review updated.");
                });
        case 5 ->
            run(
                () -> {
                  ui.displayReviews(service.getMyReviews());
                  var id = InputUtil.readUUID("Review ID");
                  if (InputUtil.readConfirm("Delete review?")) {
                    service.deleteReview(id);
                    OutputUtil.printSuccess("Review deleted.");
                  }
                });
      }
    }
  }

  public void showAdminMenu() {
    while (true) {
      OutputUtil.printHeader("REVIEW MODERATION");
      OutputUtil.println(" [1] All reviews\n [2] Product reviews\n [3] Remove review\n [0] Back");
      switch (InputUtil.readInt("Choose menu", 0, 3)) {
        case 0 -> {
          return;
        }
        case 1 -> run(() -> ui.displayReviews(service.getAllReviews()));
        case 2 -> handleViewProductReviews();
        case 3 ->
            run(
                () -> {
                  ui.displayReviews(service.getAllReviews());
                  var id = InputUtil.readUUID("Review ID");
                  if (InputUtil.readConfirm("Remove review?")) {
                    service.deleteReview(id);
                    OutputUtil.printSuccess("Review removed.");
                  }
                });
      }
    }
  }
}

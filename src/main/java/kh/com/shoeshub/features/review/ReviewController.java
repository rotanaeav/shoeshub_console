package kh.com.shoeshub.features.review;

import java.util.UUID;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;
import kh.com.shoeshub.features.review.service.ReviewService;
import kh.com.shoeshub.utils.*;
import org.nocrala.tools.texttablefmt.Table;

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
    OutputUtil.printSubHeader("SELECT PRODUCT");
    Table table = TableUtil.createTable(4, "#", "SKU", "PRODUCT NAME", "PRICE ($)");
    for (int i = 0; i < list.size(); i++) {
      var p = list.get(i);
      table.addCell(String.valueOf(i + 1));
      table.addCell(p.getSku() != null ? p.getSku() : "-");
      table.addCell(p.getName() != null ? p.getName() : "-");
      table.addCell(p.getPrice() != null ? "$" + p.getPrice().toPlainString() : "$0.00");
    }
    TableUtil.render(table);
    int n = InputUtil.readInt("Product # (0 to back)", 0, list.size());
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
          ui.displayProductReviews(service.getProductReviews(id));
          OutputUtil.printInfo("Average rating: " + ReviewUI.formatAverageRating(service.getAverageRating(id)));
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
                  (short) InputUtil.readInt("Rating (1-5)", 1, 5),
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
        case 3 -> run(() -> ui.displayMyReviews(service.getMyReviews()));
        case 4 ->
            run(
                () -> {
                  var list = service.getMyReviews();
                  if (list.isEmpty()) {
                    OutputUtil.printInfo("You have no reviews to edit.");
                    return;
                  }
                  ui.displayMyReviews(list);
                  int choice = InputUtil.readInt("Select review # to edit (0 to cancel)", 0, list.size());
                  if (choice == 0) return;
                  var selected = list.get(choice - 1);
                  service.updateReview(
                      selected.getId(),
                      (short) InputUtil.readInt("New rating (1-5)", 1, 5),
                      InputUtil.readText("New comment"));
                  OutputUtil.printSuccess("Review updated.");
                });
        case 5 ->
            run(
                () -> {
                  var list = service.getMyReviews();
                  if (list.isEmpty()) {
                    OutputUtil.printInfo("You have no reviews to delete.");
                    return;
                  }
                  ui.displayMyReviews(list);
                  int choice = InputUtil.readInt("Select review # to delete (0 to cancel)", 0, list.size());
                  if (choice == 0) return;
                  var selected = list.get(choice - 1);
                  String prodName = selected.getProductName() != null ? selected.getProductName() : "this product";
                  if (InputUtil.readConfirm("Delete review for " + prodName + "?")) {
                    service.deleteReview(selected.getId());
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
        case 1 -> run(() -> ui.displayAllReviews(service.getAllReviews()));
        case 2 -> handleViewProductReviews();
        case 3 ->
            run(
                () -> {
                  var list = service.getAllReviews();
                  if (list.isEmpty()) {
                    OutputUtil.printInfo("No reviews to remove.");
                    return;
                  }
                  ui.displayAllReviews(list);
                  int choice = InputUtil.readInt("Select review # to remove (0 to cancel)", 0, list.size());
                  if (choice == 0) return;
                  var selected = list.get(choice - 1);
                  String reviewer = selected.getUserName() != null ? selected.getUserName() : "user";
                  if (InputUtil.readConfirm("Remove review by " + reviewer + "?")) {
                    service.deleteReview(selected.getId());
                    OutputUtil.printSuccess("Review removed.");
                  }
                });
      }
    }
  }
}

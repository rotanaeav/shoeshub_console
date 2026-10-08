package kh.com.shoeshub.features.review;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kh.com.shoeshub.utils.*;
import org.nocrala.tools.texttablefmt.Table;

public class ReviewUI {
  private static final DateTimeFormatter DATE_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public static String formatStars(short rating) {
    int stars = Math.max(1, Math.min(5, (int) rating));
    return "★".repeat(stars) + "☆".repeat(5 - stars) + " (" + stars + "/5)";
  }

  public static String formatAverageRating(double avg) {
    int rounded = (int) Math.round(avg);
    int stars = Math.max(0, Math.min(5, rounded));
    return "%.2f/5 ".formatted(avg) + "★".repeat(stars) + "☆".repeat(5 - stars);
  }

  private static String formatDate(Timestamp ts) {
    if (ts == null) return "-";
    return ts.toLocalDateTime().format(DATE_FMT);
  }

  public void displayProductReviews(List<Review> reviews) {
    if (reviews.isEmpty()) {
      OutputUtil.printInfo("No reviews yet for this product.");
      return;
    }
    Table table = TableUtil.createTable(5, "#", "CUSTOMER", "RATING", "COMMENT", "DATE");
    for (int i = 0; i < reviews.size(); i++) {
      Review r = reviews.get(i);
      table.addCell(String.valueOf(i + 1));
      table.addCell(r.getUserName() != null ? r.getUserName() : "Anonymous");
      table.addCell(formatStars(r.getRating()));
      table.addCell(r.getComment() == null ? "" : r.getComment().replaceAll("[\\r\\n]", " "));
      table.addCell(formatDate(r.getCreatedAt()));
    }
    TableUtil.render(table);
  }

  public void displayMyReviews(List<Review> reviews) {
    if (reviews.isEmpty()) {
      OutputUtil.printInfo("You have not submitted any reviews yet.");
      return;
    }
    Table table = TableUtil.createTable(5, "#", "PRODUCT", "RATING", "COMMENT", "DATE");
    for (int i = 0; i < reviews.size(); i++) {
      Review r = reviews.get(i);
      table.addCell(String.valueOf(i + 1));
      table.addCell(r.getProductName() != null ? r.getProductName() : "-");
      table.addCell(formatStars(r.getRating()));
      table.addCell(r.getComment() == null ? "" : r.getComment().replaceAll("[\\r\\n]", " "));
      table.addCell(formatDate(r.getCreatedAt()));
    }
    TableUtil.render(table);
  }

  public void displayAllReviews(List<Review> reviews) {
    if (reviews.isEmpty()) {
      OutputUtil.printInfo("No reviews found.");
      return;
    }
    Table table = TableUtil.createTable(6, "#", "PRODUCT", "CUSTOMER", "RATING", "COMMENT", "DATE");
    for (int i = 0; i < reviews.size(); i++) {
      Review r = reviews.get(i);
      table.addCell(String.valueOf(i + 1));
      table.addCell(r.getProductName() != null ? r.getProductName() : "-");
      table.addCell(r.getUserName() != null ? r.getUserName() : "Anonymous");
      table.addCell(formatStars(r.getRating()));
      table.addCell(r.getComment() == null ? "" : r.getComment().replaceAll("[\\r\\n]", " "));
      table.addCell(formatDate(r.getCreatedAt()));
    }
    TableUtil.render(table);
  }

  public void displayReviews(List<Review> reviews) {
    displayProductReviews(reviews);
  }
}

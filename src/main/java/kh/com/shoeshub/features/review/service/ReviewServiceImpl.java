package kh.com.shoeshub.features.review.service;

import java.util.*;
import kh.com.shoeshub.authorize.AuthorizationService;
import kh.com.shoeshub.exception.*;
import kh.com.shoeshub.features.product.repository.ProductRepository;
import kh.com.shoeshub.features.review.*;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;
import kh.com.shoeshub.features.review.repository.ReviewRepository;
import kh.com.shoeshub.features.user.UserRole;

public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviews;
  private final ProductRepository products;
  private final AuthorizationService auth;

  public ReviewServiceImpl(
      ReviewRepository reviews, ProductRepository products, AuthorizationService auth) {
    this.reviews = reviews;
    this.products = products;
    this.auth = auth;
  }

  private void validate(short rating, String comment) {
    if (rating < 1 || rating > 5) throw new ValidationException("Rating must be from 1 to 5.");
    if (comment != null && comment.length() > 2000)
      throw new ValidationException("Comment must be at most 2000 characters.");
  }

  public Review addReview(CreateReviewRequest r) {
    var user = auth.requireAnyRole(UserRole.CUSTOMER);
    if (r == null || r.productId() == null) throw new ValidationException("Product is required.");
    validate(r.rating(), r.comment());
    var product =
        products
            .findById(r.productId())
            .orElseThrow(() -> new NotFoundException("Product not found."));
    if (!product.isActive() || product.isDeleted())
      throw new BusinessException("Product is unavailable for new reviews.");
    return reviews.save(
        Review.builder()
            .productId(r.productId())
            .userId(user.id())
            .rating(r.rating())
            .comment(r.comment() == null ? null : r.comment().trim())
            .build());
  }

  public Review updateReview(UUID id, short rating, String comment) {
    var user = auth.requireAnyRole(UserRole.CUSTOMER);
    validate(rating, comment);
    var review = reviews.findById(id).orElseThrow(() -> new NotFoundException("Review not found."));
    if (!review.getUserId().equals(user.id()))
      throw new SecurityException("You can only edit your own reviews.");
    review.setRating(rating);
    review.setComment(comment == null ? null : comment.trim());
    return reviews.update(id, review);
  }

  public void deleteReview(UUID id) {
    var user = auth.requireAnyRole(UserRole.CUSTOMER, UserRole.ADMIN);
    var review = reviews.findById(id).orElseThrow(() -> new NotFoundException("Review not found."));
    if (user.role() != UserRole.ADMIN && !review.getUserId().equals(user.id()))
      throw new SecurityException("You can only delete your own reviews.");
    reviews.deleteById(id);
  }

  public List<Review> getProductReviews(UUID id) {
    if (id == null) throw new ValidationException("Product is required.");
    products.findById(id).orElseThrow(() -> new NotFoundException("Product not found."));
    return reviews.findByProductId(id);
  }

  public double getAverageRating(UUID id) {
    getProductReviews(id);
    return reviews.getAverageRating(id);
  }

  public List<Review> getMyReviews() {
    return reviews.findByUserId(auth.requireAnyRole(UserRole.CUSTOMER).id());
  }

  public List<Review> getAllReviews() {
    auth.requireAnyRole(UserRole.ADMIN);
    return reviews.findAll();
  }
}

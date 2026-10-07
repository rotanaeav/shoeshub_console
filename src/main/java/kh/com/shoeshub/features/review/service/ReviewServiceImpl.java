package kh.com.shoeshub.features.review.service;

import kh.com.shoeshub.features.review.Review;
import kh.com.shoeshub.features.review.dto.CreateReviewRequest;
import kh.com.shoeshub.features.review.repository.ReviewRepository;
import kh.com.shoeshub.features.review.repository.ReviewRepositoryImpl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();

    public ReviewServiceImpl() { this(new ReviewRepositoryImpl()); }
    public ReviewServiceImpl(ReviewRepository repository) { this.reviewRepository= Objects.requireNonNull(repository); }
    private void customer(UUID user) {
        if (user==null || !reviewRepository.isActiveCustomer(user))
            throw new ValidationException("Please log in as an active customer to manage reviews.");
    }
    private void product(UUID product) {
        if (product==null) throw new ValidationException("Product ID is required.");
        if (!reviewRepository.productExists(product)) throw new NotFoundException("Active product not found.");
    }
    private void validate(CreateReviewRequest r) {
        if (r==null) throw new ValidationException("Review details are required.");
        product(r.getProductId());
        if (r.getRating()<1 || r.getRating()>5) throw new ValidationException("Rating must be between 1 and 5.");
        if (r.getComment()!=null && r.getComment().length()>2000)
            throw new ValidationException("Comment must be 2000 characters or fewer.");
    }
    private Review values(UUID user, CreateReviewRequest r) {
        Review review=new Review(); review.setUserId(user); review.setProductId(r.getProductId());
        review.setRating((short)r.getRating()); review.setComment(r.getComment()==null ? "" : r.getComment().strip());
        return review;
    }
    @Override public Review addReview(UUID user,CreateReviewRequest r) {
        customer(user); validate(r);
        if (reviewRepository.findByUserAndProduct(user,r.getProductId()).isPresent())
            throw new ValidationException("You already reviewed this product. Choose Edit.");
        return reviewRepository.save(values(user,r));
    }
    @Override public Review updateReview(UUID user,CreateReviewRequest r) {
        customer(user); validate(r);
        Review old=reviewRepository.findByUserAndProduct(user,r.getProductId())
                .orElseThrow(() -> new NotFoundException("You have not reviewed this product yet."));
        return reviewRepository.update(old.getId(),values(user,r));
    }
    @Override public void deleteReview(UUID user,UUID productId) {
        customer(user);
        if (productId==null) throw new ValidationException("Product ID is required.");
        Review old=reviewRepository.findByUserAndProduct(user,productId)
                .orElseThrow(() -> new NotFoundException("You have not reviewed this product yet."));
        reviewRepository.deleteOwned(old.getId(),user);
    }
    @Override public List<Review> getReviewsByProduct(UUID id) { product(id); return reviewRepository.findByProductId(id); }
    @Override public double getAverageRating(UUID id) { product(id); return reviewRepository.getAverageRating(id); }
}

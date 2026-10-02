package kh.com.shoeshub.features.review.service;

import kh.com.shoeshub.features.review.repository.ReviewRepository;
import kh.com.shoeshub.features.review.repository.ReviewRepositoryImpl;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository = new ReviewRepositoryImpl();

    // TODO: Implement review business logic
}

package kh.com.shoeshub.features.review.dto;

import java.util.UUID;

public record CreateReviewRequest(UUID productId, short rating, String comment) {}

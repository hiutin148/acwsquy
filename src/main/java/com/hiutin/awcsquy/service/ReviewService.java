package com.hiutin.awcsquy.service;

import java.util.List;

import com.hiutin.awcsquy.dto.request.ReviewRequest;
import com.hiutin.awcsquy.dto.response.ReviewResponse;

public interface ReviewService {
    ReviewResponse addReview(ReviewRequest reviewRequest);

    List<ReviewResponse> getReviewsByProductId(Long productId);

    void deleteReview(Long reviewId); // Admin or user who wrote it

    boolean canUserReviewProduct(Long userId, Long productId);
}
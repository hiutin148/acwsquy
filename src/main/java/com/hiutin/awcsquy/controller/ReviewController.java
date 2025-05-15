package com.hiutin.awcsquy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hiutin.awcsquy.dto.request.ReviewRequest;
import com.hiutin.awcsquy.dto.response.MessageResponse;
import com.hiutin.awcsquy.dto.response.ReviewResponse;
import com.hiutin.awcsquy.service.ReviewService;

import java.util.List;

@Tag(name = "Review Management", description = "APIs for managing product reviews")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Add a review for a product", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.addReview(reviewRequest);
        return new ResponseEntity<>(reviewResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all reviews for a specific product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Delete a review", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')") // Buyer can delete their own, Admin can delete any
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(new MessageResponse("Review deleted successfully."));
    }

    @Operation(summary = "Check if current user can review a product", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/product/{productId}/can-review")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Boolean> canUserReview(@PathVariable Long productId, @RequestHeader("Authorization") String token) {
        // Note: Getting userId from token or SecurityContext can be done in service
        // For simplicity here, assume service handles current user
        boolean canReview = reviewService.canUserReviewProduct(null, productId); // Pass null for userId, service will get current user
        return ResponseEntity.ok(canReview);
    }
}
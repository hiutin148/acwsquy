package com.hiutin.awcsquy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiutin.awcsquy.dto.request.ReviewRequest;
import com.hiutin.awcsquy.dto.response.ReviewResponse;
import com.hiutin.awcsquy.entity.Order;
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.Review;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.entity.enums.OrderStatus;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.ReviewMapper;
import com.hiutin.awcsquy.repository.OrderRepository;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.ReviewRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository; // To check if user purchased the product
    @Autowired
    private ReviewMapper reviewMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest reviewRequest) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", reviewRequest.getProductId()));

        if (!canUserReviewProduct(currentUser.getId(), product.getId())) {
            throw new IllegalStateException("User has not purchased this product or already reviewed it.");
        }

        Review review = reviewMapper.toReview(reviewRequest);
        review.setUser(currentUser);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);

        // Update product's average rating
        updateProductAverageRating(product.getId());

        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream().map(reviewMapper::toReviewResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User currentUser = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Admin or the user who wrote the review can delete it
        if (!review.getUser().getId().equals(currentUser.getId()) &&
            !currentUser.getRole().name().equals("ADMIN")) {
            throw new SecurityException("User is not authorized to delete this review.");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // Update product's average rating
        updateProductAverageRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserReviewProduct(Long userId, Long productId) {
        // 1. Check if user has already reviewed this product
        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return false; // Already reviewed
        }

        // 2. Check if user has purchased and completed an order for this product
        List<Order> userOrders = orderRepository.findByUserIdAndStatus(userId, OrderStatus.COMPLETED);
        return userOrders.stream()
                .anyMatch(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(productId)));
    }

    private void updateProductAverageRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        productRepository.save(product);
    }

}
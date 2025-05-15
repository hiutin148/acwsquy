package com.hiutin.awcsquy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hiutin.awcsquy.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);

    // Kiểm tra xem user đã mua sản phẩm này chưa (cần OrderRepository và
    // OrderItemRepository)
    // Đây là một query phức tạp hơn, có thể để trong service
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Query để tính average rating
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(Long productId);
}
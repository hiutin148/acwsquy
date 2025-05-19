package com.hiutin.awcsquy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hiutin.awcsquy.entity.Order;
import com.hiutin.awcsquy.entity.enums.OrderStatus;

import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // For Admin: Get all orders with pagination
    @NonNull
    Page<Order> findAll(Pageable pageable);

    // For checking if user purchased a product (used in ReviewService)
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // For revenue statistics
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumTotalPriceByStatusAndOrderDateBetween(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

package com.hiutin.awcsquy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hiutin.awcsquy.dto.request.OrderRequest;
import com.hiutin.awcsquy.dto.response.OrderResponse;
import com.hiutin.awcsquy.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createOrderFromCart(OrderRequest orderRequest);
    OrderResponse getOrderByIdForCurrentUser(Long orderId);
    List<OrderResponse> getOrderHistoryForCurrentUser();
    OrderResponse getOrderByIdForAdmin(Long orderId); // For Admin
    Page<OrderResponse> getAllOrdersForAdmin(Pageable pageable); // For Admin
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status); // For Admin or Seller (partial)
    // Mock payment processing
    OrderResponse processPayment(Long orderId);
    // For admin revenue statistics
    BigDecimal getRevenueByDate(LocalDate date);
    BigDecimal getRevenueByMonth(int year, int month);
    BigDecimal getRevenueByYear(int year);
}
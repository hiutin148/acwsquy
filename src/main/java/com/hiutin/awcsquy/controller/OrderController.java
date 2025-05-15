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

import com.hiutin.awcsquy.dto.request.OrderRequest;
import com.hiutin.awcsquy.dto.response.OrderResponse;
import com.hiutin.awcsquy.entity.enums.OrderStatus;
import com.hiutin.awcsquy.service.OrderService;

import java.util.List;

@Tag(name = "Order Management", description = "APIs for managing orders")
@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Create a new order from the current user's cart")
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody(required = false) OrderRequest orderRequest) {
         // If orderRequest is null, create a default one or handle in service
        OrderRequest request = (orderRequest == null) ? new OrderRequest() : orderRequest;
        OrderResponse createdOrder = orderService.createOrderFromCart(request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Get order details by ID for the current user")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> getOrderByIdForCurrentUser(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdForCurrentUser(orderId));
    }

    @Operation(summary = "Get order history for the current user")
    @GetMapping("/history")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<OrderResponse>> getOrderHistoryForCurrentUser() {
        return ResponseEntity.ok(orderService.getOrderHistoryForCurrentUser());
    }

    @Operation(summary = "Process payment for an order (mock)")
    @PostMapping("/{orderId}/pay")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> processPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.processPayment(orderId));
    }

    // --- Admin specific endpoints ---
    @Operation(summary = "Get order details by ID (Admin)")
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderByIdForAdmin(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdForAdmin(orderId));
    }

    @Operation(summary = "Update order status (Admin)")
    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}
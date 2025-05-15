package com.hiutin.awcsquy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hiutin.awcsquy.dto.response.OrderResponse;
import com.hiutin.awcsquy.dto.response.ProductResponse;
import com.hiutin.awcsquy.dto.response.UserResponse;
import com.hiutin.awcsquy.service.OrderService;
import com.hiutin.awcsquy.service.ProductService;
import com.hiutin.awcsquy.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(name = "Admin Panel", description = "APIs for administrative tasks")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // --- User Management ---
    @Operation(summary = "Get all users (paginated)")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    // Add endpoints for update user role, delete user, etc.

    // --- Product Management ---
    @Operation(summary = "Get all products (paginated) - Admin view")
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProductsForAdmin(pageable));
    }
    // Admin can use existing ProductController endpoints for create/update/delete if permissions are set broadly,
    // or you can add specific admin endpoints here if different logic is needed.

    // --- Order Management ---
    @Operation(summary = "Get all orders (paginated) - Admin view")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin(pageable));
    }
    // Specific admin order management (like get by ID, update status) can be in OrderController with @PreAuthorize("hasRole('ADMIN')")

    // --- Revenue Statistics ---
    @Operation(summary = "Get total revenue for a specific date")
    @GetMapping("/revenue/daily")
    public ResponseEntity<BigDecimal> getDailyRevenue(
            @Parameter(description = "Date in YYYY-MM-DD format", required = true, example = "2023-10-26")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.getRevenueByDate(date));
    }

    @Operation(summary = "Get total revenue for a specific month")
    @GetMapping("/revenue/monthly")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(
            @Parameter(description = "Year", required = true, example = "2023") @RequestParam int year,
            @Parameter(description = "Month (1-12)", required = true, example = "10") @RequestParam int month) {
        return ResponseEntity.ok(orderService.getRevenueByMonth(year, month));
    }

    @Operation(summary = "Get total revenue for a specific year")
    @GetMapping("/revenue/yearly")
    public ResponseEntity<BigDecimal> getYearlyRevenue(
            @Parameter(description = "Year", required = true, example = "2023") @RequestParam int year) {
        return ResponseEntity.ok(orderService.getRevenueByYear(year));
    }
}

package com.hiutin.awcsquy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private CategoryInfoResponse category;
    private String brand;
    private List<String> images;
    private Double averageRating;
    private Long sellerId; // ID của người bán
    private String sellerName; // Tên người bán (optional, for display)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
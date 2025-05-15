package com.hiutin.awcsquy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage; // Một ảnh đại diện của sản phẩm
    private BigDecimal pricePerUnit;
    private Integer quantity;
    private BigDecimal subtotal;
}
package com.hiutin.awcsquy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hiutin.awcsquy.dto.request.ProductRequest;
import com.hiutin.awcsquy.dto.response.ProductResponse;

import java.math.BigDecimal;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
    Page<ProductResponse> searchProductsByName(String name, Pageable pageable);
    Page<ProductResponse> filterProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, Double minRating, Pageable pageable);
    // For admin: get all products regardless of seller
    Page<ProductResponse> getAllProductsForAdmin(Pageable pageable);
}
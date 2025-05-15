package com.hiutin.awcsquy.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // For complex queries

import com.hiutin.awcsquy.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    // You can add more specific query methods or use JpaSpecificationExecutor
    // Example for filtering:
    // Page<Product> findByCategoryAndPriceBetweenAndAverageRatingGreaterThanEqual(
    //        String category, BigDecimal minPrice, BigDecimal maxPrice, Double minRating, Pageable pageable);
}

package com.hiutin.awcsquy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hiutin.awcsquy.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
package com.hiutin.awcsquy.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hiutin.awcsquy.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    long deleteAllByCartId(Long cartId);
}
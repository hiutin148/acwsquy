package com.hiutin.awcsquy.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hiutin.awcsquy.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
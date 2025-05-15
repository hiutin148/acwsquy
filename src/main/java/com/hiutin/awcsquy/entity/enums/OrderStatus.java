package com.hiutin.awcsquy.entity.enums;


public enum OrderStatus {
    CREATED,    // Mới tạo, chờ thanh toán
    PAID,       // Đã thanh toán
    SHIPPING,   // Đang giao hàng
    COMPLETED,  // Đã hoàn thành
    CANCELED    // Đã hủy
}
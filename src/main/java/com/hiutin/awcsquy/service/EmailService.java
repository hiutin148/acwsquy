package com.hiutin.awcsquy.service;

public interface EmailService {
    void sendOrderConfirmationEmail(String to, String orderId, String customerName);
    // Có thể thêm các loại email khác: welcome, password reset, etc.
}
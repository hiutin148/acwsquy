package com.hiutin.awcsquy.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hiutin.awcsquy.service.EmailService;

@Service("mockEmailService") // Give it a qualifier if you might have a real one later
public class MockEmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(MockEmailServiceImpl.class);

    @Override
    public void sendOrderConfirmationEmail(String to, String orderId, String customerName) {
        // Simulate sending an email
        logger.info("Mock Email Sent!");
        logger.info("To: {}", to);
        logger.info("Subject: Order Confirmation #{}", orderId);
        logger.info("Body: Dear {}, your order #{} has been successfully placed and paid. Thank you for shopping with us!", customerName, orderId);
    }
}

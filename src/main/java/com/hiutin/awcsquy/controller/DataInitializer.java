package com.hiutin.awcsquy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.entity.enums.Role;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository; // Autowire ProductRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) { // Only run if no users exist
            // Create ADMIN
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(Role.ADMIN);
            admin.setAddress("123 Admin St");
            admin.setPhoneNumber("1234567890");
            userRepository.save(admin);

            // Create SELLER
            User seller = new User();
            seller.setFullName("Seller One");
            seller.setEmail("seller1@example.com");
            seller.setPassword(passwordEncoder.encode("password123"));
            seller.setRole(Role.SELLER);
            seller.setAddress("456 Seller Ave");
            seller.setPhoneNumber("0987654321");
            userRepository.save(seller);

            // Create BUYER
            User buyer = new User();
            buyer.setFullName("Buyer One");
            buyer.setEmail("buyer1@example.com");
            buyer.setPassword(passwordEncoder.encode("password123"));
            buyer.setRole(Role.BUYER);
            buyer.setAddress("789 Buyer Rd");
            buyer.setPhoneNumber("1122334455");
            userRepository.save(buyer);

            System.out.println("Initial users created.");

            // Seed some products if ProductRepository exists and is empty
            if (productRepository != null && productRepository.count() == 0 && seller != null) {
                Product product1 = new Product();
                product1.setName("Laptop Pro");
                product1.setDescription("High-end laptop for professionals");
                product1.setPrice(new BigDecimal("1200.99"));
                product1.setQuantity(50);
                product1.setCategory("Electronics");
                product1.setBrand("TechBrand");
                product1.setImages(List.of("http://example.com/laptop1.jpg", "http://example.com/laptop2.jpg"));
                product1.setSeller(seller); // Associate with the seller
                product1.setAverageRating(0.0);
                productRepository.save(product1);

                Product product2 = new Product();
                product2.setName("Wireless Mouse");
                product2.setDescription("Ergonomic wireless mouse");
                product2.setPrice(new BigDecimal("25.50"));
                product2.setQuantity(200);
                product2.setCategory("Accessories");
                product2.setBrand("AccessoryCo");
                product2.setImages(List.of("http://example.com/mouse1.jpg"));
                product2.setSeller(seller);
                product2.setAverageRating(0.0);
                productRepository.save(product2);

                System.out.println("Initial products created.");
            }
        }
    }
}
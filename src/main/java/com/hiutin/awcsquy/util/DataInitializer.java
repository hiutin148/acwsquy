package com.hiutin.awcsquy.util; // Hoặc package của bạn

// ... các import khác ...
import com.hiutin.awcsquy.entity.Category; // Import Category
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.repository.CategoryRepository; // Import CategoryRepository
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.github.slugify.Slugify; // Import Slugify

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository; // TIÊM CategoryRepository
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Slugify slg = Slugify.builder().build(); // Khởi tạo Slugify

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // ... (code tạo User như cũ) ...
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRole(com.hiutin.awcsquy.entity.enums.Role.ADMIN);
            userRepository.save(admin);

            User seller1 = new User();
            seller1.setFullName("Seller One");
            seller1.setEmail("seller1@example.com");
            seller1.setPassword(passwordEncoder.encode("password123"));
            seller1.setRole(com.hiutin.awcsquy.entity.enums.Role.SELLER);
            userRepository.save(seller1);

            System.out.println("Initial users created.");


            // Seed Categories
            if (categoryRepository.count() == 0) {
                Category electronics = new Category();
                electronics.setName("Điện Tử");
                electronics.setSlug(slg.slugify(electronics.getName()));
                electronics.setDescription("Các thiết bị điện tử tiêu dùng.");
                categoryRepository.save(electronics);

                Category phones = new Category();
                phones.setName("Điện Thoại");
                phones.setSlug(slg.slugify(phones.getName()));
                phones.setParentCategory(electronics); // Set parent
                categoryRepository.save(phones);

                Category fashion = new Category();
                fashion.setName("Thời Trang");
                fashion.setSlug(slg.slugify(fashion.getName()));
                categoryRepository.save(fashion);

                System.out.println("Initial categories created.");

                 // Seed Products (sau khi đã có categories và sellers)
                if (productRepository.count() == 0 && userRepository.findByEmail("seller1@example.com").isPresent() && categoryRepository.findByName("Điện Thoại").isPresent()) {
                    User seller = userRepository.findByEmail("seller1@example.com").get();
                    Category phoneCategory = categoryRepository.findByName("Điện Thoại").get();

                    Product product1 = new Product();
                    product1.setName("Awesome Smartphone X");
                    product1.setDescription("Latest generation smartphone with great features.");
                    product1.setPrice(new BigDecimal("699.99"));
                    product1.setQuantity(100);
                    product1.setBrand("TechBrand");
                    product1.setImages(List.of("http://example.com/phone_x1.jpg"));
                    product1.setSeller(seller);
                    product1.setCategory(phoneCategory); // Gán category
                    productRepository.save(product1);

                    Product product2 = new Product();
                    product2.setName("Stylish T-Shirt");
                    product2.setDescription("Comfortable cotton t-shirt.");
                    product2.setPrice(new BigDecimal("19.99"));
                    product2.setQuantity(200);
                    product2.setBrand("FashionCo");
                    Category fashionCategory = categoryRepository.findByName("Thời Trang").orElse(null);
                    if (fashionCategory != null) {
                         product2.setCategory(fashionCategory);
                    }
                    product2.setSeller(seller);
                    productRepository.save(product2);

                    System.out.println("Initial products created.");
                }
            }
        }
    }
}
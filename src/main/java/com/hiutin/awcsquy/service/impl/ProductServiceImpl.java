package com.hiutin.awcsquy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification; // For filtering
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hiutin.awcsquy.dto.request.ProductRequest;
import com.hiutin.awcsquy.dto.response.ProductResponse;
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.ProductMapper;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.service.ProductService;

import java.math.BigDecimal; // For price

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Get current authenticated user (seller)
        String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with email: " + sellerEmail));

        Product product = productMapper.toProduct(productRequest);
        product.setSeller(seller); // Associate product with the seller
        product.setAverageRating(0.0); // Initial average rating
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Basic authorization: only seller or admin can update
        // More granular checks can be added if needed (e.g., seller can only update their own products)
        // This is often handled by @PreAuthorize in controller or service method

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setBrand(productRequest.getBrand());
        existingProduct.setImages(productRequest.getImages());
        // Average rating should be updated via reviews, not directly here

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponse> searchProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return products.map(productMapper::toProductResponse);
    }

    // Implement filtering method using JpaSpecificationExecutor
    // Example: ProductSpecification.java
    /*
    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("category"), category);
    }
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return criteriaBuilder.conjunction();
            if (minPrice == null) return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }
    public static Specification<Product> averageRatingGreaterThanOrEqual(Double minRating) {
        return (root, query, criteriaBuilder) ->
                minRating == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }
    */
    @Override
    public Page<ProductResponse> filterProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, Double minRating, Pageable pageable) {
        // Build Specification dynamically
        Specification<Product> spec = Specification.where(null);
        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (minRating != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getAllProductsForAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toProductResponse);
    }
}
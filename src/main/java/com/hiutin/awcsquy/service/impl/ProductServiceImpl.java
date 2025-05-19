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
import com.hiutin.awcsquy.entity.Category;
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.User;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.ProductMapper;
import com.hiutin.awcsquy.repository.CategoryRepository;
import com.hiutin.awcsquy.repository.ProductRepository;
import com.hiutin.awcsquy.repository.UserRepository;
import com.hiutin.awcsquy.service.ProductService;

import java.math.BigDecimal; // For price

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        String sellerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "email", sellerEmail));

        // Lấy Category từ categoryId
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));

        Product product = productMapper.toProduct(productRequest);
        product.setSeller(seller);
        product.setCategory(category); // GÁN CATEGORY CHO SẢN PHẨM
        product.setAverageRating(0.0);
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
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Logic kiểm tra quyền sở hữu sản phẩm của seller (nếu cần)
        String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentPrincipalName).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (! (currentUser.getRole().name().equals("ADMIN") || existingProduct.getSeller().getId().equals(currentUser.getId())) ) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to update this product");
        }


        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());
        // existingProduct.setCategory(productRequest.getCategory()); // Dòng này không còn đúng
        existingProduct.setBrand(productRequest.getBrand());
        existingProduct.setImages(productRequest.getImages());

        // Cập nhật Category nếu categoryId được cung cấp và khác với category hiện tại
        if (productRequest.getCategoryId() != null &&
            (existingProduct.getCategory() == null || !existingProduct.getCategory().getId().equals(productRequest.getCategoryId()))) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productRequest.getCategoryId()));
            existingProduct.setCategory(category);
        }

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
     * public static Specification<Product> hasCategory(String category) {
     * return (root, query, criteriaBuilder) ->
     * category == null ? criteriaBuilder.conjunction() :
     * criteriaBuilder.equal(root.get("category"), category);
     * }
     * public static Specification<Product> priceBetween(BigDecimal minPrice,
     * BigDecimal maxPrice) {
     * return (root, query, criteriaBuilder) -> {
     * if (minPrice == null && maxPrice == null) return
     * criteriaBuilder.conjunction();
     * if (minPrice == null) return
     * criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
     * if (maxPrice == null) return
     * criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
     * return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
     * };
     * }
     * public static Specification<Product> averageRatingGreaterThanOrEqual(Double
     * minRating) {
     * return (root, query, criteriaBuilder) ->
     * minRating == null ? criteriaBuilder.conjunction() :
     * criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), minRating);
     * }
     */
    @Override
    public Page<ProductResponse> filterProducts(String categoryName, BigDecimal minPrice, BigDecimal maxPrice, Double minRating, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        if (categoryName != null && !categoryName.isEmpty()) {
            // Tìm category theo tên, sau đó lọc sản phẩm theo category ID
            // Điều này sẽ hiệu quả hơn nếu bạn có CategoryRepository
            Category category = categoryRepository.findByName(categoryName)
                .orElse(null); // Hoặc throw exception nếu category bắt buộc phải tồn tại
            
            if (category != null) {
                 Long categoryId = category.getId();
                 spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
            } else {
                // Nếu không tìm thấy category theo tên, có thể trả về trang rỗng hoặc xử lý khác
                // Ví dụ: return Page.empty(pageable);
                // Hoặc nếu categoryName có thể là ID, thì thử parse:
                try {
                    Long categoryId = Long.parseLong(categoryName);
                     spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
                } catch (NumberFormatException e) {
                    // không phải ID, và không tìm thấy tên, bỏ qua filter category
                }
            }
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
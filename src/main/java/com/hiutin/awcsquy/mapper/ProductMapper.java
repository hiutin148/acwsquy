package com.hiutin.awcsquy.mapper; // Hoặc package của bạn

import com.hiutin.awcsquy.dto.request.ProductRequest;
import com.hiutin.awcsquy.dto.response.ProductResponse;
import com.hiutin.awcsquy.dto.response.CategoryInfoResponse; // Import DTO mới
import com.hiutin.awcsquy.entity.Product;
import com.hiutin.awcsquy.entity.Category; // Import Category entity
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class}) // Thêm CategoryMapper vào uses nếu cần map phức tạp hơn
public interface ProductMapper {

    // CẬP NHẬT MAPPING CHO CATEGORY
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "seller.fullName", target = "sellerName")
    @Mapping(source = "category", target = "category", qualifiedByName = "categoryToCategoryInfoResponse") // Sử dụng method tùy chỉnh
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    // Khi map từ ProductRequest, categoryId sẽ được xử lý trong service để lấy Category entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true) // Category sẽ được set trong service
    Product toProduct(ProductRequest productRequest);

    // Phương thức tùy chỉnh để map Category entity sang CategoryInfoResponse
    @Named("categoryToCategoryInfoResponse")
    default CategoryInfoResponse categoryToCategoryInfoResponse(Category category) {
        if (category == null) {
            return null;
        }
        CategoryInfoResponse categoryInfo = new CategoryInfoResponse();
        categoryInfo.setId(category.getId());
        categoryInfo.setName(category.getName());
        categoryInfo.setSlug(category.getSlug());
        return categoryInfo;
    }
}
package com.hiutin.awcsquy.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private String imageUrl;
    private Long parentId;
    private String parentName; // Tên danh mục cha (nếu có)
    // private Set<CategoryResponse> subCategories; // Tùy chọn: hiển thị danh mục con đệ quy
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int productCount; // Số lượng sản phẩm trong danh mục này
}
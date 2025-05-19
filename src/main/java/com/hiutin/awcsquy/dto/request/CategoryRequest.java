package com.hiutin.awcsquy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;
    private String description;
    private String imageUrl;
    private Long parentId; // ID của danh mục cha (nếu có)
}
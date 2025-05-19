package com.hiutin.awcsquy.service; // Hoặc package của bạn

import com.hiutin.awcsquy.dto.request.CategoryRequest;
import com.hiutin.awcsquy.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse getCategoryById(Long categoryId);
    CategoryResponse getCategoryBySlug(String slug);
    List<CategoryResponse> getAllCategories(); // Có thể trả về dạng phẳng hoặc cây
    List<CategoryResponse> getRootCategories();
    List<CategoryResponse> getSubcategories(Long parentId);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
    void deleteCategory(Long categoryId);

    // Helper để lấy cây danh mục (ví dụ)
    List<CategoryResponse> getCategoryTree();
}
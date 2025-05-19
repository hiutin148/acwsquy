package com.hiutin.awcsquy.service.impl; // Hoặc package của bạn

import com.github.slugify.Slugify;
import com.hiutin.awcsquy.dto.request.CategoryRequest;
import com.hiutin.awcsquy.dto.response.CategoryResponse;
import com.hiutin.awcsquy.entity.Category;
import com.hiutin.awcsquy.exception.ResourceNotFoundException;
import com.hiutin.awcsquy.mapper.CategoryMapper;
import com.hiutin.awcsquy.repository.CategoryRepository;
import com.hiutin.awcsquy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private final Slugify slg = Slugify.builder().build(); // Khởi tạo Slugify

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toCategory(categoryRequest);
        category.setSlug(slg.slugify(categoryRequest.getName()));

        if (categoryRequest.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", categoryRequest.getParentId()));
            category.setParentCategory(parentCategory);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        // Cách đơn giản là trả về danh sách phẳng
        // Để trả về dạng cây, cần logic phức tạp hơn (xem getCategoryTree)
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoryResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return categoryMapper.toCategoryResponseList(rootCategories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(Long parentId) {
        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent Category", "id", parentId);
        }
        List<Category> subcategories = categoryRepository.findByParentCategoryId(parentId);
        return categoryMapper.toCategoryResponseList(subcategories);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());
        existingCategory.setImageUrl(categoryRequest.getImageUrl());
        existingCategory.setSlug(slg.slugify(categoryRequest.getName())); // Cập nhật slug nếu tên thay đổi

        if (categoryRequest.getParentId() != null) {
            if (categoryRequest.getParentId().equals(existingCategory.getId())) {
                throw new IllegalArgumentException("Category cannot be its own parent.");
            }
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", categoryRequest.getParentId()));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        // Logic xử lý khi xóa category:
        // 1. Kiểm tra xem có sản phẩm nào thuộc category này không?
        //    Nếu có, không cho xóa hoặc set category_id của sản phẩm thành null/default.
        //    Ví dụ: if (!category.getProducts().isEmpty()) { throw new IllegalStateException("Cannot delete category with products."); }
        // 2. Kiểm tra xem có subcategories nào không?
        //    Nếu có, không cho xóa hoặc set parent_id của subcategories thành null.
        //    Ví dụ: if (!category.getSubCategories().isEmpty()) { throw new IllegalStateException("Cannot delete category with subcategories."); }
        // Quyết định này phụ thuộc vào yêu cầu nghiệp vụ của bạn.
        // Ví dụ đơn giản là cho phép xóa (cần cẩn thận với khóa ngoại và cascade):
        if (!category.getProducts().isEmpty()){
             throw new IllegalStateException("Cannot delete category. There are products associated with this category.");
        }
        if (!category.getSubCategories().isEmpty()) {
            throw new IllegalStateException("Cannot delete category. It has sub-categories. Please delete or re-assign sub-categories first.");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        // Đây là một cách đơn giản để xây dựng cây, có thể tối ưu hơn cho lượng dữ liệu lớn
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .map(this::mapCategoryToResponseWithSubcategories)
                .collect(Collectors.toList());
    }

    // Phương thức đệ quy để map category và subcategories của nó
    // Cẩn thận với lazy loading và hiệu năng nếu cây quá sâu hoặc nhiều dữ liệu
    private CategoryResponse mapCategoryToResponseWithSubcategories(Category category) {
        CategoryResponse response = categoryMapper.toCategoryResponse(category); // Mapper cơ bản
        // Tải và map subcategories nếu cần (và nếu CategoryResponse có trường subCategories)
        // Nếu CategoryResponse của bạn có 'private Set<CategoryResponse> subCategories;', bạn cần sửa CategoryMapper.
        // Hiện tại CategoryResponse không có, nên phần này chỉ là ví dụ logic:
        /*
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            Set<CategoryResponse> subResponses = category.getSubCategories().stream()
                    .map(this::mapCategoryToResponseWithSubcategories) // Gọi đệ quy
                    .collect(Collectors.toSet());
            response.setSubCategories(subResponses);
        }
        */
        return response;
    }
}
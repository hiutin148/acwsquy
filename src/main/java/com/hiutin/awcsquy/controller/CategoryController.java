package com.hiutin.awcsquy.controller; // Hoặc package của bạn

import com.hiutin.awcsquy.dto.request.CategoryRequest;
import com.hiutin.awcsquy.dto.response.CategoryResponse;
import com.hiutin.awcsquy.dto.response.MessageResponse;
import com.hiutin.awcsquy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category Management", description = "APIs for managing product categories")
@RestController
// Cân nhắc prefix chung, ví dụ /api
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ================= ADMIN APIs =================
    @Operation(summary = "Create a new category (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing category (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                         @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete a category (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully"));
    }

    // ================= PUBLIC APIs =================
    @Operation(summary = "Get all categories (can be a flat list or tree structure)")
    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(name = "tree", defaultValue = "false", required = false) boolean treeView) {
        List<CategoryResponse> categories;
        if (treeView) {
            categories = categoryService.getCategoryTree();
        } else {
            categories = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get a category by its ID or Slug")
    @GetMapping("/api/categories/{identifier}") // identifier can be ID or Slug
    public ResponseEntity<CategoryResponse> getCategoryByIdOrSlug(@PathVariable String identifier) {
        CategoryResponse category;
        try {
            Long id = Long.parseLong(identifier);
            category = categoryService.getCategoryById(id);
        } catch (NumberFormatException e) {
            category = categoryService.getCategoryBySlug(identifier);
        }
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Get root categories (categories with no parent)")
    @GetMapping("/api/categories/roots")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }


    @Operation(summary = "Get subcategories of a given parent category ID")
    @GetMapping("/api/categories/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryResponse> subcategories = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(subcategories);
    }
}
package com.hiutin.awcsquy.mapper;

import com.hiutin.awcsquy.dto.request.CategoryRequest;
import com.hiutin.awcsquy.dto.response.CategoryResponse;
import com.hiutin.awcsquy.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring") // Không cần 'uses' nếu không map đệ quy subCategories ngay trong này
public interface CategoryMapper {

    @Mapping(source = "parentCategory.id", target = "parentId")
    @Mapping(source = "parentCategory.name", target = "parentName")
    @Mapping(target = "productCount", expression = "java(category.getProducts() != null ? category.getProducts().size() : 0)")
    // @Mapping(target = "subCategories", qualifiedByName = "toCategoryResponseSet") // Nếu muốn map đệ quy
    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true) // Slug thường được tạo trong service
    @Mapping(target = "parentCategory", ignore = true) // Sẽ được set trong service dựa trên parentId
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toCategory(CategoryRequest categoryRequest);

    // // Nếu muốn map đệ quy subCategories (cẩn thận với lazy loading và vòng lặp vô hạn)
    // @Named("toCategoryResponseSet")
    // Set<CategoryResponse> toCategoryResponseSet(Set<Category> subCategories);
}
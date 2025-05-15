package com.hiutin.awcsquy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hiutin.awcsquy.dto.request.ProductRequest;
import com.hiutin.awcsquy.dto.response.ProductResponse;
import com.hiutin.awcsquy.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "seller.fullName", target = "sellerName")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "seller", ignore = true) // Sẽ được set trong service
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toProduct(ProductRequest productRequest);
}
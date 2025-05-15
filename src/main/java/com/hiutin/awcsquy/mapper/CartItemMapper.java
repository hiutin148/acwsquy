package com.hiutin.awcsquy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.hiutin.awcsquy.dto.response.CartItemResponse;
import com.hiutin.awcsquy.entity.CartItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "pricePerUnit")
    @Mapping(source = "product.images", target = "productImage", qualifiedByName = "getFirstImage")
    @Mapping(target = "subtotal", expression = "java(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponse toCartItemResponse(CartItem item);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);

    @Named("getFirstImage")
    default String getFirstImage(List<String> images) {
        return (images != null && !images.isEmpty()) ? images.get(0) : null;
    }
}
package com.hiutin.awcsquy.mapper;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hiutin.awcsquy.dto.response.CartResponse;
import com.hiutin.awcsquy.entity.Cart;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "items", target = "items")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartResponse toCartResponse(Cart cart);
}
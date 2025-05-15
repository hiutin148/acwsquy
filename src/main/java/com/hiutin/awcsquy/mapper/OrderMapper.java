package com.hiutin.awcsquy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hiutin.awcsquy.dto.response.OrderResponse;
import com.hiutin.awcsquy.entity.Order;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    OrderResponse toOrderResponse(Order order);
}
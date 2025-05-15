package com.hiutin.awcsquy.mapper;

import com.hiutin.awcsquy.dto.response.OrderItemResponse;
import com.hiutin.awcsquy.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal; // Giữ lại import này
import java.util.List;

@Mapper(
    componentModel = "spring",
    imports = { BigDecimal.class } // THÊM DÒNG NÀY
)
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "subtotal", expression = "java(orderItem.getPricePerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity())))")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);
}
package com.hiutin.awcsquy.mapper; // Hoặc package của bạn

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hiutin.awcsquy.dto.request.RegisterRequest;
import com.hiutin.awcsquy.dto.response.UserResponse;
import com.hiutin.awcsquy.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // KHÔNG cần @Mapping(target = "authorities", ignore = true) ở đây
    // vì UserResponse không có trường "authorities".
    // MapStruct sẽ tự động bỏ qua nếu không tìm thấy target.
    UserResponse toUserResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Thêm ignore cho "authorities" ở đây vì User entity có phương thức getAuthorities(),
    // và chúng ta không muốn set nó từ RegisterRequest.
    @Mapping(target = "authorities", ignore = true)
    User registerRequestToUser(RegisterRequest registerRequest);
}
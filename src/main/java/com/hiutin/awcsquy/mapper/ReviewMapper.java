package com.hiutin.awcsquy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hiutin.awcsquy.dto.request.ReviewRequest;
import com.hiutin.awcsquy.dto.response.ReviewResponse;
import com.hiutin.awcsquy.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "product.id", target = "productId")
    ReviewResponse toReviewResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Sẽ được set trong service
    @Mapping(target = "product", ignore = true) // Sẽ được set trong service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toReview(ReviewRequest reviewRequest);
}
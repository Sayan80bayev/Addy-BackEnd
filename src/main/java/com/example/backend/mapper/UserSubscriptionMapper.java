package com.example.backend.mapper;

import com.example.backend.dto.request.SubcribeRequest;
import com.example.backend.dto.response.UserSubscriptionResponse;
import com.example.backend.model.UserSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserSubscriptionMapper {

    UserSubscriptionMapper INSTANCE = Mappers.getMapper(UserSubscriptionMapper.class);

    // Mapping SubcribeRequest to UserSubscription Entity
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "user", ignore = true) // Handle user mapping separately
    @Mapping(target = "ad", ignore = true) // Handle advertisement mapping separately
    UserSubscription toEntity(SubcribeRequest subcribeRequest);

    // Mapping UserSubscription Entity to UserSubscriptionResponse
    @Mapping(target = "id", source = "userSubscription.id")
    @Mapping(target = "userId", source = "userSubscription.user.id") // Assuming User has a field 'id'
    @Mapping(target = "advertisementId", source = "userSubscription.ad.id") // Assuming Advertisement has a field 'id'
    UserSubscriptionResponse toResponse(UserSubscription userSubscription);

    // Mapping for a list of UserSubscription to a list of UserSubscriptionResponse
    List<UserSubscriptionResponse> toResponseList(List<UserSubscription> userSubscriptions);
}

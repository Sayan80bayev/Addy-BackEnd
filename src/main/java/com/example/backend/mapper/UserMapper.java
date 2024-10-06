package com.example.backend.mapper;

import com.example.backend.dto.response.UserResponse;
import com.example.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapping User to UserResponse
    UserResponse toResponse(User user);

    // Mapping for a list of User to a list of UserResponse
    List<UserResponse> toResponseList(List<User> users);
}

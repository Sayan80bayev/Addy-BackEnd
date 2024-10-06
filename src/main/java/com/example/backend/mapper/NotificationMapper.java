package com.example.backend.mapper;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    // Mapping Notification to NotificationResponse
    NotificationResponse toResponse(Notification notification);

    // Mapping for a list of Notification to a list of NotificationResponse
    List<NotificationResponse> toResponseList(List<Notification> notifications);
}

package com.example.backend.dto.request;

import java.util.UUID;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscribeRequest {
    @NotNull
    UUID id;

    @NotBlank(message = "{subscribeRequest.email.notBlank}")
    String email;
}

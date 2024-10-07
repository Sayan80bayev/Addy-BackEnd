package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    @NotNull(message = "{jwtRequest.email.notNull}")
    private String email;

    @NotBlank(message = "{jwtRequest.password.notBlank}")
    private String password;
}

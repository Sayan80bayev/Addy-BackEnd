package com.example.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "{registerRequest.username.notBlank}")
    private String username;

    @NotBlank(message = "{registerRequest.email.notBlank}")
    @Email(message = "{registerRequest.email.emailValid}")
    private String email;

    @NotBlank(message = "{registerRequest.password.notBlank}")
    @Size(min = 8, message = "{registerRequest.password.size}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "{registerRequest.password.pattern}")
    private String password;
}

package com.example.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "{userUpdateRequest.email.notBlank}")
    @Email(message = "{userUpdateRequest.email.emailValid}")
    private String email;

    @NotBlank(message = "{userUpdateRequest.username.notBlank}")
    private String username;

    @NotBlank(message = "{userUpdateRequest.password.notBlank}")
    @Size(min = 8, message = "{userUpdateRequest.password.size}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "{userUpdateRequest.password.pattern}")
    private String password;

    @NotBlank(message = "{userUpdateRequest.confirmPassword.notBlank}")
    @Size(min = 8, message = "{userUpdateRequest.confirmPassword.size}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "{userUpdateRequest.confirmPassword.pattern}")
    private String confirmPassword;
}

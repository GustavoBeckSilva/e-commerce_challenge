package com.compass.e_commerce_challenge.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
	@Email
    @NotBlank
    private String email;
}

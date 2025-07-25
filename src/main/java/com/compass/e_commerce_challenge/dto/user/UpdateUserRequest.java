package com.compass.e_commerce_challenge.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
	@NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String address;

    private Boolean active;
}

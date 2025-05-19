package com.compass.e_commerce_challenge.dto.auth;

import java.util.Set;

import com.compass.e_commerce_challenge.entity.UserRoles;

import lombok.Data;

@Data
public class JwtResponse {
	private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Set<UserRoles> roles;
}

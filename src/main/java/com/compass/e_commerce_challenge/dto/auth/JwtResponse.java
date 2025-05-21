package com.compass.e_commerce_challenge.dto.auth;

import java.time.LocalDateTime;
import java.util.Set;

import com.compass.e_commerce_challenge.entity.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
	private String accessToken;
    private String tokenType = "Bearer";
    private LocalDateTime expiresAt;
    private Set<UserRoles> roles;
}

package com.compass.e_commerce_challenge.dto.user;

import java.time.LocalDateTime;
import java.util.Set;

import com.compass.e_commerce_challenge.entity.UserRoles;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
	private Long id;
    private String username;
    private String email;
    private String address;
    private Boolean active;
    private Set<UserRoles> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

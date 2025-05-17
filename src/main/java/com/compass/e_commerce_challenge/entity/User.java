package com.compass.e_commerce_challenge.entity;

import java.time.LocalDateTime;
import java.util.*;

public class User {
	
	private Long id;
	private String username;
	private String email;
	private String password;
	private String address;
	private Boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private List<PasswordResetToken> resetTokens;
	private Set<UserRoles> roles;
	private List<Order> orders;
	private Cart cart;
	
}

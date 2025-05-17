package com.compass.e_commerce_challenge.entity;

import java.time.*;

public class PasswordResetToken {
 
	private Long id;
    private String token;
    private LocalDateTime expiryDate;
    private User user;
    
}

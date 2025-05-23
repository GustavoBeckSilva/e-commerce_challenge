package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.auth.ForgotPasswordRequest;
import com.compass.e_commerce_challenge.dto.auth.JwtResponse;
import com.compass.e_commerce_challenge.dto.auth.LoginRequest;
import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.dto.auth.ResetPasswordRequest;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;


public interface AuthService {
    
	// Shared
    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);
    ApiResponse<?> resetPassword(ResetPasswordRequest request);
	JwtResponse authenticate(LoginRequest request); 
	
	// Client
	ApiResponse<?> registerClient(RegisterRequest request); 
	
	// Admin
    ApiResponse<?> registerAdmin(RegisterRequest request);
	    
}

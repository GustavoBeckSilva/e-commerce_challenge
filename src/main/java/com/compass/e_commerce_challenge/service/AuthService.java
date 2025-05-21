package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.auth.JwtResponse;
import com.compass.e_commerce_challenge.dto.auth.LoginRequest;
import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;


public interface AuthService {
    ApiResponse<?> register(RegisterRequest request);
    JwtResponse authenticate(LoginRequest request);
}

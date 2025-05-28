package com.compass.e_commerce_challenge.service;

public interface EmailService {
	
	/*
	 * Shared
	 */
	
    void sendPasswordResetEmail(String to, String token);
    
}

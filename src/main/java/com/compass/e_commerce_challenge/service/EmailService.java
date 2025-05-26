package com.compass.e_commerce_challenge.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}

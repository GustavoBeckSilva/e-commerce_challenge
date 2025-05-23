package com.compass.e_commerce_challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.service.AuthService;

@Component
public class InitialDataLoader implements ApplicationRunner {
	
	private final AuthService authService;
    private final String adminUsername;
    private final String adminEmail;
    private final String adminPassword;

    public InitialDataLoader(AuthService authService,
                             @Value("${app.admin.username}") String adminUsername,
                             @Value("${app.admin.email}") String adminEmail,
                             @Value("${app.admin.password}") String adminPassword) {
        this.authService = authService;
        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {

        RegisterRequest req = new RegisterRequest();
        req.setUsername(adminUsername);
        req.setEmail(adminEmail);
        req.setPassword(adminPassword);
        req.setAddress("Admin Address");
        authService.registerAdmin(req);
        
    }
}
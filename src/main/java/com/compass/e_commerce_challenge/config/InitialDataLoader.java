package com.compass.e_commerce_challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.service.AuthService;
import com.compass.e_commerce_challenge.repository.UserRepository; 

@Component
public class InitialDataLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);
    private final AuthService authService;
    private final UserRepository userRepository; 
    private final String adminUsername;
    private final String adminEmail;
    private final String adminPassword;

    public InitialDataLoader(AuthService authService,
                             UserRepository userRepository,
                             @Value("${app.admin.username}") String adminUsername,
                             @Value("${app.admin.email}") String adminEmail,
                             @Value("${app.admin.password}") String adminPassword) {
        this.authService = authService;
        this.userRepository = userRepository; 
        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!userRepository.existsByEmail(adminEmail) && !userRepository.existsByUsername(adminUsername)) {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(adminUsername);
            req.setEmail(adminEmail);
            req.setPassword(adminPassword);
            req.setAddress("Admin Address Default");
            
            try {
                authService.registerAdmin(req);
                logger.info("Initial admin user created successfully with email: {}", adminEmail);
            } catch (Exception e) {
                logger.error("Error creating initial admin user: {}. This might be expected if another process created it concurrently.", e.getMessage());
            }
        } else {
            logger.info("Initial admin user with email {} or username {} already exists. Skipping creation.", adminEmail, adminUsername); // Logging informando que o admin já existe
        }
    }
}

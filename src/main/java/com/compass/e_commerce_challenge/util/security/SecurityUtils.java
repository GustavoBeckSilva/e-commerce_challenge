package com.compass.e_commerce_challenge.util.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long getCurrentUserId(UserRepository userRepo) {
        Object principal = SecurityContextHolder.getContext()
                                         .getAuthentication()
                                         .getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user", "email", email + " (Principal type: " + principal.getClass().getSimpleName() + ")"));
        return user.getId();
    }
}
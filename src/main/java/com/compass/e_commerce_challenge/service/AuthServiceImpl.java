package com.compass.e_commerce_challenge.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compass.e_commerce_challenge.dto.auth.ForgotPasswordRequest;
import com.compass.e_commerce_challenge.dto.auth.JwtResponse;
import com.compass.e_commerce_challenge.dto.auth.LoginRequest;
import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.dto.auth.ResetPasswordRequest;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.entity.Cart;
import com.compass.e_commerce_challenge.entity.PasswordResetToken;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.entity.UserRoles;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.PasswordResetTokenRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.InvalidTokenException;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;
import com.compass.e_commerce_challenge.util.exceptions.UserAlreadyExistsException;
import com.compass.e_commerce_challenge.util.security.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {
 	
	@Autowired
    private UserRepository userRepository;

	@Autowired
    private PasswordResetTokenRepository tokenRepository;
	
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    @Override
    public ApiResponse<?> registerClient(RegisterRequest request) {
        
    	if (userRepository.existsByEmail(request.getEmail()))
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' already registered.");
        
        if (userRepository.existsByUsername(request.getUsername())) 
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already in use.");
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .active(true)
                .roles(Set.of(UserRoles.ROLE_CLIENT))
                .build();

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        userRepository.save(user);
        cartRepository.save(cart);

        return ApiResponse.success("User registered successfully");
    }
    
    @Override
    public ApiResponse<?> registerAdmin(RegisterRequest request) {
        
    	if (userRepository.existsByEmail(request.getEmail()))
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' already registered.");
        
    	if (userRepository.existsByUsername(request.getUsername()))
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already in use.");
        
        User admin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .active(true)
                .roles(Set.of(UserRoles.ROLE_ADMIN))
                .build();
        
        userRepository.save(admin);
        
        return ApiResponse.success("Administrator created successfully");

    }

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid credentials. Please check your email and password.");
        }

        String token = jwtUtils.generateToken(authentication);
        LocalDateTime expiry = jwtUtils.getExpirationDateFromToken(token);

        Set<UserRoles> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRoles::valueOf)
                .collect(Collectors.toSet());

        	return new JwtResponse(token, "Bearer", expiry, roles);
	    }
    
    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
    	User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken prt = PasswordResetToken.builder()
                .token(token)
                .expiryDate(expiry)
                .user(user)
                .build();
        tokenRepository.save(prt);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return ApiResponse.success("A password reset link has been sent to your email.");
    }
    
    @Override
    @Transactional
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        PasswordResetToken prt = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Password reset token invalid or not found."));
        
        if (prt.isExpired()) {
            tokenRepository.delete(prt);
            throw new InvalidTokenException("Password reset token expired.");
        }
        
        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(prt);
        return ApiResponse.success("Password updated successfully");
   
    }
    
}


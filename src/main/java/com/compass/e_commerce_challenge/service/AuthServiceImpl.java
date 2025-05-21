package com.compass.e_commerce_challenge.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.compass.e_commerce_challenge.dto.auth.JwtResponse;
import com.compass.e_commerce_challenge.dto.auth.LoginRequest;
import com.compass.e_commerce_challenge.dto.auth.RegisterRequest;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.entity.Cart;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.entity.UserRoles;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.security.JwtUtils;

@Service
public class AuthServiceImpl implements AuthService {
 	@Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ApiResponse<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email já cadastrado");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("Username já em uso");
        }

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

        return ApiResponse.success("Usuário registrado com sucesso");
    }

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String token = jwtUtils.generateToken(authentication);
        LocalDateTime expiry = jwtUtils.getExpirationDateFromToken(token);

        @SuppressWarnings("unchecked")
        Set<UserRoles> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRoles::valueOf)
                .collect(Collectors.toSet());

        	return new JwtResponse(token, "Bearer", expiry, roles);
	    }
	}


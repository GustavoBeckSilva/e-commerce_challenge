package com.compass.e_commerce_challenge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.compass.e_commerce_challenge.service.UserDetailsServiceImpl;
import com.compass.e_commerce_challenge.util.security.AuthEntryPointJwt;
import com.compass.e_commerce_challenge.util.security.JwtAuthTokenFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	 @Autowired
	    private UserDetailsServiceImpl userDetailsService;

	    @Autowired
	    private AuthEntryPointJwt unauthorizedHandler;

	    @Autowired
	    private JwtAuthTokenFilter jwtAuthTokenFilter;

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	        return authConfig.getAuthenticationManager();
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf.disable())
	            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
	            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/auth/register", "/auth/login", "/auth/forgot-password", "/auth/reset-password")
	                .permitAll()
	                .anyRequest().authenticated()
	            );

	        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }
	}

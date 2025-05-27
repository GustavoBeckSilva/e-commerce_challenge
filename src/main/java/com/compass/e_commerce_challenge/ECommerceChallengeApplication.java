package com.compass.e_commerce_challenge;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class ECommerceChallengeApplication {

	@PostConstruct
	public void init() {
		Locale.setDefault(Locale.US);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ECommerceChallengeApplication.class, args);
	}

}

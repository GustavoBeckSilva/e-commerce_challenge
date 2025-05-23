package com.compass.e_commerce_challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class ECommerceChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceChallengeApplication.class, args);
	}

}

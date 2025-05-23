package com.compass.e_commerce_challenge.util.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    private Key key;

    @PostConstruct
    public void init() {
        try {

            String cleanedSecret = jwtSecret.replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleanedSecret);
            
            if (keyBytes.length != 32)
                throw new IllegalArgumentException("Key must be 32 bytes (256 bits)");
            
            this.key = Keys.hmacShaKeyFor(keyBytes);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding key: " + e.getMessage());
            throw e;
        }
    }


    @SuppressWarnings("deprecation")
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
	public List<String> getRolesFromToken(String token) {
    	Claims claims = parseClaims(token);
        return claims.get("roles", List.class);
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
    	Date date = parseClaims(token).getExpiration();
        return date.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String resolveToken(HttpServletRequest request) {
    	String headerVal = request.getHeader(tokenHeader);

        if (headerVal == null) {
            return null;
        }

        String trimmed = headerVal.trim();

        if (trimmed.toLowerCase().startsWith((tokenPrefix + " ").toLowerCase())) {
            return trimmed.substring((tokenPrefix + " ").length()).trim();
        }
        return null;
    }
}
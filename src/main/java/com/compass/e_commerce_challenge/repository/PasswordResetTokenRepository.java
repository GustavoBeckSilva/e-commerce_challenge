package com.compass.e_commerce_challenge.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{

	Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUserId(Long userId);
    void deleteByExpiryDateBefore(LocalDateTime cutoff);
    
}

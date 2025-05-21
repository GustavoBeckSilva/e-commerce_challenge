package com.compass.e_commerce_challenge.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);
	Page<User> findByActiveTrue(Pageable pageable);
	
	@Query("SELECT u FROM User u JOIN u.orders o GROUP BY u ORDER BY SUM(o.totalAmount) DESC")
	Page<User> findTopCustomers(Pageable pageable);
	
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
	
}

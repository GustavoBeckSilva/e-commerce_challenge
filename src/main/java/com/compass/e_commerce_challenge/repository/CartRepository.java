package com.compass.e_commerce_challenge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	
	Optional<Cart> findByUserId(Long userId);

}

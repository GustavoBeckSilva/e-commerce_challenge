package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.cart.CartItemRequest;
import com.compass.e_commerce_challenge.dto.cart.CartResponse;

public interface CartService {
	
	/*
	 * Client
	 */
	
	CartResponse addItem(Long userId, CartItemRequest request);
    CartResponse updateItem(Long userId, Long cartItemId, CartItemRequest request);
    CartResponse removeItem(Long userId, Long cartItemId);
    CartResponse getCart(Long userId);
	
}

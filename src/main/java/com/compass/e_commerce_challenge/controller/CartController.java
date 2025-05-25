package com.compass.e_commerce_challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.compass.e_commerce_challenge.dto.cart.CartItemRequest;
import com.compass.e_commerce_challenge.dto.cart.CartResponse;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.util.exceptions.BadRequestException;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody CartItemRequest request) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.addItem(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long id,
                                                   @RequestBody CartItemRequest request) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.updateItem(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.removeItem(userId, id);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("Usuário não encontrado."));
        return user.getId();
    }
}

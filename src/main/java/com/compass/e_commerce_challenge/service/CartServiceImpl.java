package com.compass.e_commerce_challenge.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compass.e_commerce_challenge.dto.cart.CartItemRequest;
import com.compass.e_commerce_challenge.dto.cart.CartItemResponse;
import com.compass.e_commerce_challenge.dto.cart.CartResponse;
import com.compass.e_commerce_challenge.entity.Cart;
import com.compass.e_commerce_challenge.entity.CartItem;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.entity.User;
import com.compass.e_commerce_challenge.repository.CartItemRepository;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
import com.compass.e_commerce_challenge.util.exceptions.InsufficientStockException;
import com.compass.e_commerce_challenge.util.exceptions.OperationNotAllowedException;
import com.compass.e_commerce_challenge.util.exceptions.ProductInactiveException;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.getProductId()));
        
        if (!product.getActive()) {
            throw new ProductInactiveException("Product '" + product.getName() + "' is inactive.");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for the product: " + product.getName() + ". Available: " + product.getStockQuantity());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock to add more units of the product: " + product.getName() + ". Available: " + product.getStockQuantity());
            }
            
            item.setQuantity(newQuantity);
        
        } else {
            CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();
            cart.getItems().add(newItem);
        }
        
        Cart saved = cartRepository.save(cart);
        return mapToCartResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, CartItemRequest request) {

    	CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart Item", "ID", cartItemId));
	    
    	if (!item.getCart().getUser().getId().equals(userId))
            throw new OperationNotAllowedException("This item in the cart does not belong to the current user.");
	    	    
    	Product product = item.getProduct();
        
    	if (!product.getActive()) {
            throw new ProductInactiveException("Product '" + product.getName() + "' is inactive.");
        }
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for the product: " + product.getName() + ". Available: " + product.getStockQuantity());
        }
	    	    
	    item.setQuantity(request.getQuantity());
	    
	    cartItemRepository.save(item);

	    return mapToCartResponse(item.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item", "ID", cartItemId));
        if (!item.getCart().getUser().getId().equals(userId)) {
        	throw new OperationNotAllowedException("Item doesn't belong to user.");
        }

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartRepository.save(cart);
        cartItemRepository.delete(item);
        
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User cart", "User ID", userId));
        return mapToCartResponse(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = modelMapper.map(cart, CartResponse.class);

        var itemResponses = cart.getItems().stream()
            .map(item -> {
                CartItemResponse dto = modelMapper.map(item, CartItemResponse.class);
                BigDecimal totalPrice = dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                dto.setTotalPrice(totalPrice);
                return dto;
            })
            .collect(Collectors.toList());
        response.setItems(itemResponses);

        BigDecimal totalAmount = itemResponses.stream()
            .map(CartItemResponse::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalAmount(totalAmount);
        return response;
    }
}

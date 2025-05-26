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
import com.compass.e_commerce_challenge.util.exceptions.BadRequestException;
import com.compass.e_commerce_challenge.repository.CartItemRepository;
import com.compass.e_commerce_challenge.repository.CartRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import com.compass.e_commerce_challenge.repository.UserRepository;
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
            .orElseThrow(() -> new BadRequestException("Product not found."));
        if (!product.getActive() || product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock or inactive product.");
        }

        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> Cart.builder()
                .user(userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("User not found.")))
                .build());

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        CartItem item = existing.orElseGet(() -> {
            CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .build();
            cart.getItems().add(newItem);
            return newItem;
        });
        int newQuantity = item.getQuantity() == null ? request.getQuantity() : item.getQuantity() + request.getQuantity();
        item.setQuantity(newQuantity);
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);

        Cart saved = cartRepository.save(cart);
        return mapToCartResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, CartItemRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new BadRequestException("Cart item not found"));
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Item doesn't belong to user.");
        }
        int delta = request.getQuantity() - item.getQuantity();
        Product product = item.getProduct();
        if (delta > 0 && product.getStockQuantity() < delta) {
            throw new BadRequestException("Insufficient stock.");
        }
        item.setQuantity(request.getQuantity());
        product.setStockQuantity(product.getStockQuantity() - delta);
        productRepository.save(product);
        cartItemRepository.save(item);

        return mapToCartResponse(item.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new BadRequestException("Cart item not found."));
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Item doesn't belong to user.");
        }
        Product product = item.getProduct();
        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        productRepository.save(product);

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new BadRequestException("Cart not found."));
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

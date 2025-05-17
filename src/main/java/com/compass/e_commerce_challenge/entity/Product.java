package com.compass.e_commerce_challenge.entity;

import java.math.*;
import java.time.*;
import java.util.*;

public class Product {
	
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Category category;

    private List<OrderItem> orderItems;

    private List<CartItem> cartItems;
}

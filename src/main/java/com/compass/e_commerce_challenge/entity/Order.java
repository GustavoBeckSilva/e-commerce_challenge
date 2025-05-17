package com.compass.e_commerce_challenge.entity;

import java.math.*;
import java.time.*;
import java.util.*;

public class Order {
    
	private Long id;

    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private OrderStatus status;

    private User user;
    private List<OrderItem> items;

}

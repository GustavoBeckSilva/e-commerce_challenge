package com.compass.e_commerce_challenge.entity;

import java.time.*;
import java.util.*;

public class Cart {
	
	private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;

    private List<CartItem> items;
}

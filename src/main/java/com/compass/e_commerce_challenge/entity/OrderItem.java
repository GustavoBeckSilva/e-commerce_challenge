package com.compass.e_commerce_challenge.entity;

import java.math.*;

public class OrderItem {

    private Long id;
    private Integer quantity;
    private BigDecimal unitPrice;

    private Order order;
    private Product product;
}

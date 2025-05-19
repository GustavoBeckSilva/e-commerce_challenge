package com.compass.e_commerce_challenge.dto.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}
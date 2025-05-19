package com.compass.e_commerce_challenge.dto.report;

import lombok.Data;

@Data
public class LowStockProductDTO {
    private Long productId;
    private String productName;
    private Integer stockQuantity;
}

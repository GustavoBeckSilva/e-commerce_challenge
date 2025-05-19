package com.compass.e_commerce_challenge.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.compass.e_commerce_challenge.dto.category.CategorySummary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
	private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean active;
    private CategorySummary category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

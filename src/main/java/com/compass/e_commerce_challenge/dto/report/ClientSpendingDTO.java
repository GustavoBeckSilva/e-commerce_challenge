package com.compass.e_commerce_challenge.dto.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ClientSpendingDTO {
    private Long userId;
    private String username;
    private BigDecimal totalSpent;
}
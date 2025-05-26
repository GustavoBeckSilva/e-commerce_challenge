package com.compass.e_commerce_challenge.dto.report;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSpendingDTO {
    private Long userId;
    private String username;
    private BigDecimal totalSpent;
}
package com.compass.e_commerce_challenge.dto.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesSummaryDTO {
    private BigDecimal totalSales;
    private BigDecimal totalProfit;
}

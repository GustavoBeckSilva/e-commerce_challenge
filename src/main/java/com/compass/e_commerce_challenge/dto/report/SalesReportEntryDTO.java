package com.compass.e_commerce_challenge.dto.report;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesReportEntryDTO {
	private String period;
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;
}

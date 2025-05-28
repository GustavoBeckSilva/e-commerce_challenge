package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.report.ClientSpendingDTO;
import com.compass.e_commerce_challenge.dto.report.LowStockProductDTO;
import com.compass.e_commerce_challenge.dto.report.PeriodRequest;
import com.compass.e_commerce_challenge.dto.report.ProductSalesDTO;
import com.compass.e_commerce_challenge.dto.report.ReportGrouping;
import com.compass.e_commerce_challenge.dto.report.SalesReportEntryDTO;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;

public interface ReportService {
	
	/*
	 * Admin
	 */
	
	PagedResponse<SalesReportEntryDTO> getSalesSummary(PeriodRequest period, ReportGrouping groupBy, PageRequestDto pageRequest);
    PagedResponse<LowStockProductDTO> getLowStockProducts(PageRequestDto pageRequest);
    PagedResponse<ProductSalesDTO> getTopSellingProducts(PeriodRequest period, PageRequestDto pageRequest);
    PagedResponse<ClientSpendingDTO> getTopClients(PeriodRequest period, PageRequestDto pageRequest);
}
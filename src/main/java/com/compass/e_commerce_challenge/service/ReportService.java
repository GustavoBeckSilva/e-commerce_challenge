package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.report.*;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;

public interface ReportService {
    SalesSummaryDTO getSalesSummary(PeriodRequest period);
    PagedResponse<LowStockProductDTO> getLowStockProducts(PageRequestDto pageRequest);
    PagedResponse<ProductSalesDTO> getTopSellingProducts(PeriodRequest period, PageRequestDto pageRequest);
    PagedResponse<ClientSpendingDTO> getTopClients(PeriodRequest period, PageRequestDto pageRequest);
}
package com.compass.e_commerce_challenge.controller;

import com.compass.e_commerce_challenge.dto.report.*;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales")
    public SalesSummaryDTO sales(@RequestParam("start") String start,
                                 @RequestParam("end") String end) {
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(LocalDateTime.parse(start));
        pr.setEnd(LocalDateTime.parse(end));
        return reportService.getSalesSummary(pr);
    }

    @GetMapping("/low-stock")
    public PagedResponse<LowStockProductDTO> lowStock(@ModelAttribute PageRequestDto pageReq) {
        return reportService.getLowStockProducts(pageReq);
    }

    @GetMapping("/top-products")
    public PagedResponse<ProductSalesDTO> topProducts(@RequestParam("start") String start,
                                                     @RequestParam("end") String end,
                                                     @ModelAttribute PageRequestDto pageReq) {
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(LocalDateTime.parse(start));
        pr.setEnd(LocalDateTime.parse(end));
        return reportService.getTopSellingProducts(pr, pageReq);
    }

    @GetMapping("/top-clients")
    public PagedResponse<ClientSpendingDTO> topClients(@RequestParam("start") String start,
                                                      @RequestParam("end") String end,
                                                      @ModelAttribute PageRequestDto pageReq) {
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(LocalDateTime.parse(start));
        pr.setEnd(LocalDateTime.parse(end));
        return reportService.getTopClients(pr, pageReq);
    }
}

package com.compass.e_commerce_challenge.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compass.e_commerce_challenge.dto.report.ClientSpendingDTO;
import com.compass.e_commerce_challenge.dto.report.LowStockProductDTO;
import com.compass.e_commerce_challenge.dto.report.PeriodRequest;
import com.compass.e_commerce_challenge.dto.report.ProductSalesDTO;
import com.compass.e_commerce_challenge.dto.report.ReportGrouping;
import com.compass.e_commerce_challenge.dto.report.SalesReportEntryDTO;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.service.ReportService;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<PagedResponse<SalesReportEntryDTO>> sales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "DAY") ReportGrouping groupBy,
            @ModelAttribute PageRequestDto pageReq) {
        
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(start);
        pr.setEnd(end);

        PagedResponse<SalesReportEntryDTO> summary = reportService.getSalesSummary(pr, groupBy, pageReq);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/low-stock")
    public PagedResponse<LowStockProductDTO> lowStock(@ModelAttribute PageRequestDto pageReq) {
        return reportService.getLowStockProducts(pageReq);
    }

    @GetMapping("/top-products")
    public PagedResponse<ProductSalesDTO> topProducts(@RequestParam String start,
                                                     @RequestParam String end,
                                                     @ModelAttribute PageRequestDto pageReq) {
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(LocalDateTime.parse(start));
        pr.setEnd(LocalDateTime.parse(end));
        return reportService.getTopSellingProducts(pr, pageReq);
    }

    @GetMapping("/top-clients")
    public PagedResponse<ClientSpendingDTO> topClients(@RequestParam String start,
                                                      @RequestParam String end,
                                                      @ModelAttribute PageRequestDto pageReq) {
        PeriodRequest pr = new PeriodRequest();
        pr.setStart(LocalDateTime.parse(start));
        pr.setEnd(LocalDateTime.parse(end));
        return reportService.getTopClients(pr, pageReq);
    }
}

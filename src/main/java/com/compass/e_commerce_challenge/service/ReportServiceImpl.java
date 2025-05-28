package com.compass.e_commerce_challenge.service;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.compass.e_commerce_challenge.config.ReportProperties;
import com.compass.e_commerce_challenge.dto.report.ClientSpendingDTO;
import com.compass.e_commerce_challenge.dto.report.LowStockProductDTO;
import com.compass.e_commerce_challenge.dto.report.PeriodRequest;
import com.compass.e_commerce_challenge.dto.report.ProductSalesDTO;
import com.compass.e_commerce_challenge.dto.report.ReportGrouping;
import com.compass.e_commerce_challenge.dto.report.SalesReportEntryDTO;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.repository.OrderItemRepository;
import com.compass.e_commerce_challenge.repository.OrderRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ReportProperties props;

    @Override
    public PagedResponse<SalesReportEntryDTO> getSalesSummary(PeriodRequest period, ReportGrouping groupBy, PageRequestDto pageRequest) {
        List<Object[]> salesData;
        List<Object[]> profitData;
        String dateFormat;

        switch (groupBy) {
            case WEEK:
                salesData = orderRepository.sumWeeklySales(period.getStart(), period.getEnd());
                profitData = orderRepository.sumWeeklyProfit(period.getStart(), period.getEnd());
                dateFormat = "yyyy-'W'ww"; 
                break;
            case MONTH:
                salesData = orderRepository.sumMonthlySales(period.getStart(), period.getEnd());
                profitData = orderRepository.sumMonthlyProfit(period.getStart(), period.getEnd());
                dateFormat = "yyyy-MM";
                break;
            case DAY:
            default:
                salesData = orderRepository.sumDailySales(period.getStart(), period.getEnd());
                profitData = orderRepository.sumDailyProfit(period.getStart(), period.getEnd());
                dateFormat = "yyyy-MM-dd";
                break;
        }

        Map<String, SalesReportEntryDTO> reportMap = new LinkedHashMap<>();
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        for (Object[] row : salesData) {
            Timestamp timestamp = (Timestamp) row[0];
            String periodKey = sdf.format(timestamp);
            
            SalesReportEntryDTO entry = reportMap.computeIfAbsent(periodKey, k -> {
                SalesReportEntryDTO newEntry = new SalesReportEntryDTO();
                newEntry.setPeriod(k);
                return newEntry;
            });
            entry.setTotalSales((BigDecimal) row[1]);
        }

        for (Object[] row : profitData) {
            Timestamp timestamp = (Timestamp) row[0];
            String periodKey = sdf.format(timestamp);

            SalesReportEntryDTO entry = reportMap.get(periodKey);
            if (entry != null) {
                entry.setTotalProfit((BigDecimal) row[1]);
            }
        }


        List<SalesReportEntryDTO> fullReport = new ArrayList<SalesReportEntryDTO>(reportMap.values());
        
        PageRequest pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), fullReport.size());

        List<SalesReportEntryDTO> pageContent = (start > fullReport.size())
            ? new ArrayList<>()
            : fullReport.subList(start, end);

        Page<SalesReportEntryDTO> reportPage = new PageImpl<>(pageContent, pageable, fullReport.size());

        return PagedResponse.<SalesReportEntryDTO>builder()
                .content(reportPage.getContent())
                .page(reportPage.getNumber())
                .size(reportPage.getSize())
                .totalElements(reportPage.getTotalElements())
                .totalPages(reportPage.getTotalPages())
                .last(reportPage.isLast())
                .build();
    }

    @Override
    public PagedResponse<LowStockProductDTO> getLowStockProducts(PageRequestDto pageRequest) {
        PageRequest pg = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        Integer threshold = props.getLowStockThreshold();

        Page<Product> productPage = productRepo.findLowStock(threshold, pg);

        Page<LowStockProductDTO> dtoPage = productPage.map(p -> {
            LowStockProductDTO d = new LowStockProductDTO();
            d.setProductId(p.getId());
            d.setProductName(p.getName());
            d.setStockQuantity(p.getStockQuantity());
            return d;
        });

        return PagedResponse.<LowStockProductDTO>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Override
    public PagedResponse<ProductSalesDTO> getTopSellingProducts(PeriodRequest period, PageRequestDto pageRequest) {
    	PageRequest pg = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());

        List<ProductSalesDTO> content = orderItemRepo.findTopProducts(period.getStart(), period.getEnd(), pg);
        long total = orderItemRepo.countTopProducts(period.getStart(), period.getEnd());
        Page<ProductSalesDTO> page = new PageImpl<>(content, pg, total);
        PagedResponse<ProductSalesDTO> resp = new PagedResponse<>();
        resp.setContent(page.getContent());
        resp.setPage(page.getNumber());
        resp.setSize(page.getSize());
        resp.setTotalElements(page.getTotalElements());
        resp.setTotalPages(page.getTotalPages());
        resp.setLast(page.isLast());
        return resp;
    }

    @Override
    public PagedResponse<ClientSpendingDTO> getTopClients(PeriodRequest period, PageRequestDto pageRequest) {
    	PageRequest pg = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());

        List<ClientSpendingDTO> content = orderItemRepo.findTopClients(period.getStart(), period.getEnd(), pg);
        long total = orderItemRepo.countTopClients(period.getStart(), period.getEnd());
        Page<ClientSpendingDTO> page = new PageImpl<>(content, pg, total);
        PagedResponse<ClientSpendingDTO> resp = new PagedResponse<>();
        resp.setContent(page.getContent());
        resp.setPage(page.getNumber());
        resp.setSize(page.getSize());
        resp.setTotalElements(page.getTotalElements());
        resp.setTotalPages(page.getTotalPages());
        resp.setLast(page.isLast());
        return resp;
    }
}
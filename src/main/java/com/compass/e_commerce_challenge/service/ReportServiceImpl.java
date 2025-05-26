package com.compass.e_commerce_challenge.service;
import com.compass.e_commerce_challenge.config.ReportProperties;
import com.compass.e_commerce_challenge.dto.report.*;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.repository.OrderItemRepository;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ReportProperties props;

    @Override
    public SalesSummaryDTO getSalesSummary(PeriodRequest period) {
        BigDecimal totalSales = orderItemRepo.sumTotalSales(period.getStart(), period.getEnd());
        BigDecimal totalProfit = orderItemRepo.sumTotalProfit(period.getStart(), period.getEnd());
        SalesSummaryDTO dto = new SalesSummaryDTO();
        dto.setTotalSales(totalSales != null ? totalSales : BigDecimal.ZERO);
        dto.setTotalProfit(totalProfit != null ? totalProfit : BigDecimal.ZERO);
        return dto;
    }

    @Override
    public PagedResponse<LowStockProductDTO> getLowStockProducts(PageRequestDto pageRequest) {
    	PageRequest pg = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());

        Integer threshold = props.getLowStockThreshold();
        List<Product> content = productRepo.findLowStock(threshold, pg);
        long total = productRepo.countLowStock(threshold);
        Page<LowStockProductDTO> page = new PageImpl<>(
            content.stream().map(p -> {
                LowStockProductDTO d = new LowStockProductDTO();
                d.setProductId(p.getId()); d.setProductName(p.getName()); d.setStockQuantity(p.getStockQuantity());
                return d;
            }).collect(Collectors.toList()), pg, total);
        PagedResponse<LowStockProductDTO> resp = new PagedResponse<>();
        resp.setContent(page.getContent());
        resp.setPage(page.getNumber());
        resp.setSize(page.getSize());
        resp.setTotalElements(page.getTotalElements());
        resp.setTotalPages(page.getTotalPages());
        resp.setLast(page.isLast());
        return resp;
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
package com.compass.e_commerce_challenge.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.dto.report.ClientSpendingDTO;
import com.compass.e_commerce_challenge.dto.report.ProductSalesDTO;
import com.compass.e_commerce_challenge.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
	
	List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    
    @Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE o.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalSales(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query("SELECT SUM((oi.unitPrice - oi.product.costPrice) * oi.quantity) FROM OrderItem oi JOIN oi.order o WHERE o.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalProfit(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("SELECT new com.compass.e_commerce_challenge.dto.report.ProductSalesDTO(p.id, p.name, SUM(oi.quantity), SUM(oi.unitPrice * oi.quantity)) " +
           "FROM OrderItem oi JOIN oi.product p JOIN oi.order o " +
           "WHERE o.createdAt BETWEEN :start AND :end " +
           "GROUP BY p.id, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<ProductSalesDTO> findTopProducts(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          Pageable pageable);

    @Query("SELECT COUNT(DISTINCT p.id) FROM OrderItem oi JOIN oi.product p JOIN oi.order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countTopProducts(@Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    @Query("SELECT new com.compass.e_commerce_challenge.dto.report.ClientSpendingDTO(" +
    		  "   u.id, u.username, SUM(oi.unitPrice * oi.quantity)) " +
    		  "FROM OrderItem oi " +
    		  " JOIN oi.order o " +
    		  " JOIN o.user u " +
    		  "WHERE o.createdAt BETWEEN :start AND :end " +
    		  "GROUP BY u.id, u.username " +
    		  "ORDER BY SUM(oi.unitPrice * oi.quantity) DESC")
	List<ClientSpendingDTO> findTopClients(@Param("start") LocalDateTime start, @Param("end")   LocalDateTime end, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT u.id) FROM OrderItem oi JOIN oi.order o JOIN o.user u WHERE o.createdAt BETWEEN :start AND :end")
    Long countTopClients(@Param("start") LocalDateTime start,
                         @Param("end") LocalDateTime end);
}

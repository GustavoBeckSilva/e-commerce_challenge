package com.compass.e_commerce_challenge.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.entity.Order;
import com.compass.e_commerce_challenge.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	
	Page<Order> findByUserId(Long userId, Pageable pageable);
	Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query(value = "SELECT DATE_TRUNC('day', o.created_at) AS dia, SUM(o.total_amount) AS total " + // MODIFICADO
            "FROM tb_order o " +
            "WHERE o.created_at BETWEEN :start AND :end " +
            "GROUP BY DATE_TRUNC('day', o.created_at)", nativeQuery = true)
    List<Object[]> sumDailySales(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            SELECT
            DATE_TRUNC('week', o.created_at) AS semana,
            SUM(o.total_amount) AS total
            FROM tb_order o
            WHERE o.created_at BETWEEN :start AND :end
            GROUP BY DATE_TRUNC('week', o.created_at)
          """, nativeQuery = true)
    List<Object[]> sumWeeklySales(LocalDateTime start, LocalDateTime end);

    @Query( value = "SELECT DATE_TRUNC('month', o.created_at) AS mes, SUM(o.total_amount) AS total " +
              "FROM tb_order o " +
              "WHERE o.created_at BETWEEN :start AND :end " +
              "GROUP BY DATE_TRUNC('month', o.created_at)", nativeQuery = true
    )
    List<Object[]> sumMonthlySales(LocalDateTime start, LocalDateTime end);
    
    @Query(value = """
            SELECT
            DATE_TRUNC('day', o.created_at) AS dia,
            SUM( (oi.unit_price - p.cost_price) * oi.quantity ) AS lucro
            FROM tb_order o
            JOIN tb_order_item oi ON oi.order_id = o.id
            JOIN tb_product p      ON p.id = oi.product_id
            WHERE o.created_at BETWEEN :start AND :end
            GROUP BY DATE_TRUNC('day', o.created_at)
          """, nativeQuery = true)
	List<Object[]> sumDailyProfit(LocalDateTime start, LocalDateTime end);

    @Query(value = """
            SELECT 
            DATE_TRUNC('week', o.created_at) AS dia,
            SUM( (oi.unit_price - p.cost_price) * oi.quantity ) AS lucro
            FROM tb_order o
            JOIN tb_order_item oi ON oi.order_id = o.id
            JOIN tb_product p      ON p.id = oi.product_id
            WHERE o.created_at BETWEEN :start AND :end
            GROUP BY DATE_TRUNC('week', o.created_at)
          """, nativeQuery = true)
    List<Object[]> sumWeeklyProfit(LocalDateTime start, LocalDateTime end);
            
     @Query(value = """
            SELECT 
            DATE_TRUNC('month', o.created_at) AS dia,
            SUM( (oi.unit_price - p.cost_price) * oi.quantity ) AS lucro
            FROM tb_order o
            JOIN tb_order_item oi ON oi.order_id = o.id
            JOIN tb_product p      ON p.id = oi.product_id
            WHERE o.created_at BETWEEN :start AND :end
            GROUP BY DATE_TRUNC('month', o.created_at)
          """, nativeQuery = true)
     List<Object[]> sumMonthlyProfit(LocalDateTime start, LocalDateTime end);
        
}

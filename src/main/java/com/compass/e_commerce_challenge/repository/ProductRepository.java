package com.compass.e_commerce_challenge.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compass.e_commerce_challenge.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	Page<Product> findByActiveTrueAndStockQuantityGreaterThan(int minStock, Pageable pageable);
	Page<Product> findAll(Pageable pageable);
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.orderItems oi GROUP BY p ORDER BY SUM(oi.quantity) DESC")
    Page<Product> findTopSellingProducts(Pageable pageable);

    Page<Product> findByStockQuantityLessThan(int threshold, Pageable pageable);
    
    boolean existsByOrderItems_Product_Id(Long productId);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold")
    List<Product> findLowStock(@Param("threshold") Integer threshold, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < :threshold")
    Long countLowStock(@Param("threshold") Integer threshold);


}

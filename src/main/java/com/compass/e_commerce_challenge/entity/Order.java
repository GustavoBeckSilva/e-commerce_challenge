package com.compass.e_commerce_challenge.entity;

import java.math.*;
import java.time.*;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "tb_order")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Order {

	/**
	 * Properties
	*/

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
	@ToString.Include
    private BigDecimal totalAmount;
    
	@Column(name = "created_at", nullable = false, updatable = false)
	@ToString.Include
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	@ToString.Include
	private LocalDateTime updatedAt;

	/**
	 * Associations
	*/
	
	@NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
	private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    /**
	 * Auxiliary methods
	*/
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal calculateTotalAmount() {
    	return items.stream().map(OrderItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}

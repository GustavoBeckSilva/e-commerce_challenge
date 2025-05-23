package com.compass.e_commerce_challenge.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tb_product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {
	
	// Properties *************************************************************************************

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
    private Long id;

	@NotBlank
	@Column(nullable = false)
    @ToString.Include
    private String name;

	@NotBlank
	@Column(nullable = false)
    @ToString.Include
    private String description;
    
	@NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    @ToString.Include
    private BigDecimal price;
	
	@NotNull
    @PositiveOrZero
    @Column(name = "cost_price", nullable = false, precision = 19, scale = 2)
    @ToString.Include
	private BigDecimal costPrice;
    
	@NotNull
    @Min(0)
    @Column(nullable = false)
    @ToString.Include
    private Integer stockQuantity;
    
    @NotNull
    @Column(nullable = false)
    @ToString.Include
    private Boolean active;

	@Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
    @ToString.Include
	private LocalDateTime updatedAt;

	// Associations *************************************************************************************
    
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
	private List<OrderItem> orderItems = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
	private List<CartItem> cartItems = new ArrayList<>();
    
	// Auxiliary methods ********************************************************************************

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
    
}

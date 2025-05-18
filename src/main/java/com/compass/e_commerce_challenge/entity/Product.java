package com.compass.e_commerce_challenge.entity;

import java.math.*;
import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
    
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
	private Set<Category> categories = new HashSet<>();

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

package com.compass.e_commerce_challenge.entity;

import java.time.*;
import java.util.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_cart")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Cart {
	
	/**
	 * Properties
	*/
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(name = "created_at", nullable = false, updatable = false)
	@ToString.Include
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	@ToString.Include
    private LocalDateTime updatedAt;
    
	/**
	 * Associations
	*/
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
	private List<CartItem> items = new ArrayList<>();

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
	
}

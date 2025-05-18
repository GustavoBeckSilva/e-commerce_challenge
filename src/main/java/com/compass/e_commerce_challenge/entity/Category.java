package com.compass.e_commerce_challenge.entity;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Category {

	// Properties *************************************************************************************
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
    private Long id;

	@NotBlank
	@Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

	@Column
	private String description;

	@Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;
    
	@Column(name = "updated_at", nullable = false)
    @ToString.Include
	private LocalDateTime updatedAt;

	// Associations *************************************************************************************
	
	@ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
	@Builder.Default
    private List<Product> products = new ArrayList<>();
    
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

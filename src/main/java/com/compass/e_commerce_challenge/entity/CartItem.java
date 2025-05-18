package com.compass.e_commerce_challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_cart_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CartItem {

	// Properties *************************************************************************************
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@NotNull
    @Min(1)
    @Column(nullable = false)
    @ToString.Include
    private Integer quantity;
    
	// Associations *************************************************************************************
	
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
	// Auxiliary methods ********************************************************************************

    
}

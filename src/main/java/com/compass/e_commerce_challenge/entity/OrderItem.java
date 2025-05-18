package com.compass.e_commerce_challenge.entity;

import java.math.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "tb_order_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OrderItem {
	
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
    
	@NotNull
	@Positive
	@Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
	@ToString.Include
    private BigDecimal unitPrice;

    // Associations *************************************************************************************

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
	// Auxiliary methods ********************************************************************************

    public BigDecimal getSubTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
}

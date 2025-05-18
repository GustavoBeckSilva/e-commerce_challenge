package com.compass.e_commerce_challenge.entity;

import java.time.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_password_reset_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PasswordResetToken {
 
	// Properties *************************************************************************************
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@NotBlank
	@Column(nullable = false, unique = true)
	@ToString.Include
    private String token;
    
	@Column(name= "expiry_date", nullable = false)
	@ToString.Include
    private LocalDateTime expiryDate;

	// Associations *************************************************************************************

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

	// Auxiliary methods ******************************************************************************** 
    
    public boolean isExpired() {
    	return LocalDateTime.now().isAfter(this.expiryDate);
    }
    
}

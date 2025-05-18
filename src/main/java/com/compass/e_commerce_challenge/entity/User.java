package com.compass.e_commerce_challenge.entity;

import java.time.*;
import java.util.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {
	
	// Properties *************************************************************************************

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
    @ToString.Include
	private String username;

    @NotBlank
    @Email
	@Column(nullable = false, unique = true)
    @ToString.Include
    private String email;
	
    @NotBlank
	@Column(nullable = false)
	private String password;
	
    @NotBlank
	@Column(nullable = false)
	private String address;
	
    @NotNull
	@Column(nullable = false)
	private Boolean active;
	    
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	// Associations *************************************************************************************
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<PasswordResetToken> resetTokens;
	
	@Column(name = "role", nullable = false)
	@ElementCollection
	@CollectionTable
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@ToString.Include
	private Set<UserRoles> roles = new HashSet<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Order> orders;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private Cart cart;

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

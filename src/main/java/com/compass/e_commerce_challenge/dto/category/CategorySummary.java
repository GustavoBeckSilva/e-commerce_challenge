package com.compass.e_commerce_challenge.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySummary {
	private Long id;
    private String name;
}

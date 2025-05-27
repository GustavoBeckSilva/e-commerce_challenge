package com.compass.e_commerce_challenge.dto.shared;

import lombok.Data;

@Data
public class PageRequestDto {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String direction = "ASC";
}

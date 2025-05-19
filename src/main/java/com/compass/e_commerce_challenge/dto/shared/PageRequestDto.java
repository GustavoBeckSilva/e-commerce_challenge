package com.compass.e_commerce_challenge.dto.shared;

import lombok.Data;

@Data
public class PageRequestDto {
    private int page;
    private int size;
    private String sortBy;
    private String direction;
}

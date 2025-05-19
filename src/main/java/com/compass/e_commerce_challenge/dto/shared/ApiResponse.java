package com.compass.e_commerce_challenge.dto.shared;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
}
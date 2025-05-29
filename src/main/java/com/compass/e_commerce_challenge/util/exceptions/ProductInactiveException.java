package com.compass.e_commerce_challenge.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductInactiveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProductInactiveException(String message) {
        super(message);
    }
}

package com.compass.e_commerce_challenge.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmptyCartException(String message) {
        super(message);
    }
}

package com.compass.e_commerce_challenge.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperationNotAllowedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperationNotAllowedException(String message) {
        super(message);
    }
}

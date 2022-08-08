package com.vraj.socialmediaapp.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class StatusException extends RuntimeException {

    private final HttpStatus httpStatus;

    public StatusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}

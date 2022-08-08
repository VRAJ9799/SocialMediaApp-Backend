package com.vraj.socialmediaapp.models.commons;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApiError<T> {
    public T error;
    private String path;
    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private boolean isSuccess = false;
    private Date timestamp = new Date();

    public ApiError(T error, String path) {
        this.error = error;
        this.path = path;
    }

    public ApiError(T error, String path, int status) {
        this.error = error;
        this.path = path;
        this.status = status;
    }
}


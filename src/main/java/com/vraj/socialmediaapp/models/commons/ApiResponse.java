package com.vraj.socialmediaapp.models.commons;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private int status = HttpStatus.OK.value();
    private boolean isSuccess = true;
    private Date timestamp = new Date();

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(T data, int status) {
        this.data = data;
        this.status = status;
    }
}


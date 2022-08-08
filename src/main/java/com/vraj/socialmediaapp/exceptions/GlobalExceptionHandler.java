package com.vraj.socialmediaapp.exceptions;

import com.vraj.socialmediaapp.models.commons.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class, StatusException.class})
    public ResponseEntity<?> handleException(Exception exception, WebRequest webRequest) {
        ApiError<String> apiError = new ApiError<>(exception.getMessage(), webRequest.getDescription(false));
        if (exception instanceof StatusException statusException) {
            apiError.setStatus(statusException.getHttpStatus().value());
        }
        return ResponseEntity.status(apiError.getStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            List<String> msg = errorMessages.getOrDefault(error.getField(), new ArrayList<>());
            msg.add(error.getDefaultMessage());
            errorMessages.put(error.getField(), msg);
        });
        ApiError<Map<String, List<String>>> apiError = new ApiError<>(
                errorMessages,
                request.getDescription(false),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }
}

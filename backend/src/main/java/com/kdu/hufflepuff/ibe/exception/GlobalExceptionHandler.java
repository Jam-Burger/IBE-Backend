package com.kdu.hufflepuff.ibe.exception;

import com.kdu.hufflepuff.ibe.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("exceptionType", "ResourceNotFoundException");
        logContext.put("message", ex.getMessage());
        logContext.put("statusCode", HttpStatus.NOT_FOUND.value());
        
        log.warn("Resource not found: {}", logContext);
        
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequest(InvalidRequestException ex) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("exceptionType", "InvalidRequestException");
        logContext.put("message", ex.getMessage());
        logContext.put("statusCode", HttpStatus.BAD_REQUEST.value());
        
        log.warn("Invalid request: {}", logContext);
        
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        fieldErrors.forEach(fieldError -> errorMessage.append(fieldError.getDefaultMessage()).append(". "));
        
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("exceptionType", "MethodArgumentNotValidException");
        logContext.put("message", errorMessage.toString());
        logContext.put("statusCode", HttpStatus.BAD_REQUEST.value());
        logContext.put("fieldErrors", fieldErrors.stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "rejectedValue", error.getRejectedValue(),
                "message", error.getDefaultMessage()
            ))
            .toList());
        
        log.warn("Validation failure: {}", logContext);
        
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(errorMessage.toString())
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("exceptionType", ex.getClass().getName());
        logContext.put("message", ex.getMessage());
        logContext.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        logContext.put("stackTrace", ex.getStackTrace());
        
        log.error("Unhandled exception: {}", logContext, ex);
        
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred: " + ex.getMessage())
            .build();
    }
}


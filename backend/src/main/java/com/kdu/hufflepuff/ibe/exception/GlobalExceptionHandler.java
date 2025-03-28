package com.kdu.hufflepuff.ibe.exception;

import com.kdu.hufflepuff.ibe.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidImageTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidImageType(InvalidImageTypeException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ImageUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleImageUpload(ImageUploadException ex) {
        log.error("Failed to upload image: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("Failed to upload image: " + ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ConfigUpdateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleConfigUpdate(ConfigUpdateException ex) {
        log.error("Failed to update configuration: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("Failed to update configuration: " + ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        fieldErrors.forEach(fieldError -> errorMessage.append(fieldError.getDefaultMessage()).append(". "));

        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(errorMessage.toString())
            .build()
            .send();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred: " + ex.getMessage())
            .build()
            .send();
    }
}


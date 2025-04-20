package com.kdu.hufflepuff.ibe.exception;

import com.kdu.hufflepuff.ibe.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorResponse> handleBookingException(BookingException ex) {
        return ErrorResponse.builder()
            .statusCode(determineHttpStatus(ex))
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(InvalidImageTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageTypeException(InvalidImageTypeException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(InvalidConfigException.class)
    public ResponseEntity<ErrorResponse> handleInvalidConfigException(InvalidConfigException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(ConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigNotFoundException(ConfigNotFoundException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageUploadException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(ConfigUpdateException.class)
    public ResponseEntity<ErrorResponse> handleConfigUpdateException(ConfigUpdateException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(InvalidPromoCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPromoCodeException(InvalidPromoCodeException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(errors.values().iterator().next()) // Return only the first validation error
            .build()
            .send();
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidationExceptions(HandlerMethodValidationException ex) {
        log.error("Method validation error: {}", ex.getMessage());

        // Extract the error message from the exception
        String message = ex.getMessage();

        // If the message contains parameter information, extract it
        if (message.contains("parameter")) {
            // The message typically contains information about which parameter failed validation
            return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST)
                .message(message)
                .build()
                .send();
        }

        // Default message if we can't extract parameter information
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message("Validation failed: " + message)
            .build()
            .send();
    }

    @ExceptionHandler(MiscellaneousException.class)
    public ResponseEntity<ErrorResponse> handleMiscellaneousException(MiscellaneousException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.FORBIDDEN)
            .message(getRootCauseMessage(ex))
            .build()
            .send();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    private HttpStatus determineHttpStatus(BookingException ex) {
        if (ex instanceof RoomAvailabilityException || ex instanceof PaymentException || ex instanceof PromotionException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof BookingOperationException && ex.getMessage().contains("not found")) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }
}


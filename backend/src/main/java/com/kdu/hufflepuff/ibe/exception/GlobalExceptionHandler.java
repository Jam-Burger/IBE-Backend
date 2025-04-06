package com.kdu.hufflepuff.ibe.exception;

import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ApiResponse<String>> handleBookingException(BookingException ex) {
        log.error("Booking exception occurred: {}", ex.getMessage(), ex);

        return ApiResponse.<String>builder()
            .statusCode(determineHttpStatus(ex))
            .message("Booking operation failed")
            .data(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(RoomAvailabilityException.class)
    public ResponseEntity<ApiResponse<String>> handleRoomAvailabilityException(RoomAvailabilityException ex) {
        log.error("Room availability exception occurred: {}", ex.getMessage(), ex);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message("Room availability issue")
            .data(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<String>> handlePaymentException(PaymentException ex) {
        log.error("Payment exception occurred: {}", ex.getMessage(), ex);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message("Payment processing failed")
            .data(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(BookingOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleBookingOperationException(BookingOperationException ex) {
        log.error("Booking operation exception occurred: {}", ex.getMessage(), ex);

        HttpStatus status = ex.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        return ApiResponse.<String>builder()
            .statusCode(status)
            .message("Booking operation failed")
            .data(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(PromotionException.class)
    public ResponseEntity<ApiResponse<String>> handlePromotionException(PromotionException ex) {
        log.error("Promotion exception occurred: {}", ex.getMessage(), ex);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message("Promotion issue")
            .data(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation exception occurred: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.<Map<String, String>>builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message("Validation failed")
            .data(errors)
            .build()
            .send();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        return ApiResponse.<String>builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred")
            .data(ex.getMessage())
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
}


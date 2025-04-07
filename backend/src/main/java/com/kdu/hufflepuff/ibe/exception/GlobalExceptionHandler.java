package com.kdu.hufflepuff.ibe.exception;

import com.kdu.hufflepuff.ibe.model.response.ErrorResponse;
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
    public ResponseEntity<ErrorResponse> handleBookingException(BookingException ex) {
        return ErrorResponse.builder()
            .statusCode(determineHttpStatus(ex))
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(RoomAvailabilityException.class)
    public ResponseEntity<ErrorResponse> handleRoomAvailabilityException(RoomAvailabilityException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(BookingOperationException.class)
    public ResponseEntity<ErrorResponse> handleBookingOperationException(BookingOperationException ex) {
        HttpStatus status = ex.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        return ErrorResponse.builder()
            .statusCode(status)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(PromotionException.class)
    public ResponseEntity<ErrorResponse> handlePromotionException(PromotionException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidImageTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImageTypeException(InvalidImageTypeException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidConfigException.class)
    public ResponseEntity<ErrorResponse> handleInvalidConfigException(InvalidConfigException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigNotFoundException(ConfigNotFoundException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageUploadException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ConfigUpdateException.class)
    public ResponseEntity<ErrorResponse> handleConfigUpdateException(ConfigUpdateException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(InvalidPromoCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPromoCodeException(InvalidPromoCodeException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .build()
            .send();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
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
            .message("Validation failed: " + errors)
            .build()
            .send();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred: " + ex.getMessage())
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


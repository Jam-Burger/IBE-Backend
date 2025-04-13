package com.kdu.hufflepuff.ibe.exception;

import lombok.Getter;

@Getter
public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
} 
package com.kdu.hufflepuff.ibe.exception;

import lombok.Getter;

@Getter
public class PaymentException extends BookingException {
    private PaymentException(String message) {
        super(message);
    }

    private PaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PaymentException paymentFailed(String reason) {
        return new PaymentException(String.format("Payment processing failed: %s", reason));
    }

    public static PaymentException paymentFailed(String reason, Throwable cause) {
        return new PaymentException(String.format("Payment processing failed: %s", reason), cause);
    }

    public static PaymentException invalidPaymentInfo(String reason) {
        return new PaymentException(
            String.format("Invalid payment information provided: %s", reason));

    }
} 
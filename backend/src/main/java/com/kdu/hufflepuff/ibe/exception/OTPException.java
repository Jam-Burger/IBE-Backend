package com.kdu.hufflepuff.ibe.exception;


public class OTPException extends RuntimeException {
    private OTPException(String message) {
        super(message);
    }

    public static OTPException invalidEmail(String email) {
        return new OTPException("Invalid OTP for email: " + email);
    }

    public static OTPException invalidOtp(String otp) {
        return new OTPException("Invalid OTP: " + otp);
    }

    public static OTPException otpExpired(String email) {
        return new OTPException("OTP expired for email: " + email);
    }

    public static OTPException maxAttemptsReached(String email) {
        return new OTPException("Max attempts reached for email: " + email);
    }

    public static OTPException otpAlreadyVerified(String email) {
        return new OTPException("OTP already verified for email: " + email);
    }

    public static OTPException otpNotFound(Long id) {
        return new OTPException("OTP not found for ID: " + id);
    }
}

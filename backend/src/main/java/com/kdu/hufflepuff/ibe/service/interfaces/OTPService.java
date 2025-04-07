package com.kdu.hufflepuff.ibe.service.interfaces;

import jakarta.mail.MessagingException;

public interface OTPService {

    /**
     * Sends an OTP email using a Thymeleaf template to the provided email address.
     * @param toEmail recipient email
     * @param otp the OTP string
     * @throws MessagingException if email sending fails
     */
    void sendOtpMail(String toEmail, String otp) throws MessagingException;

    /**
     * Generates a new OTP or returns an existing valid one.
     * @param email the email for which OTP is to be generated
     * @return the OTP string
     */
    String generateOtp(String email);

    /**
     * Verifies the provided OTP for the given email.
     * @param email the email associated with the OTP
     * @param otp the OTP to verify
     * @return true if valid, false otherwise
     */
    boolean verifyOtp(String email, String otp);
}

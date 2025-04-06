package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.entity.OTPEntity;
import com.kdu.hufflepuff.ibe.repository.jpa.OTPRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    private final OTPRepository otpRepository;

    private final JavaMailSender mailSender;

    @Autowired
    public OTPServiceImpl(OTPRepository otpRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    public void sendOtpMail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt will expire in " + OTP_EXPIRY_MINUTES + " minutes.");

        mailSender.send(message);
    }

    public String generateOtp(String email) {
        Optional<OTPEntity> existingOtpOpt = otpRepository.findByEmail(email);

        if (existingOtpOpt.isPresent()) {
            OTPEntity existingOtp = existingOtpOpt.get();

            if (existingOtp.getExpirationTime().isAfter(LocalDateTime.now())) {
                sendOtpMail(email, existingOtp.getOtpNumber());
                return existingOtp.getOtpNumber();
            } else {
                otpRepository.delete(existingOtp);
            }
        }

        String newOtp = String.format("%06d", new Random().nextInt(999999));

        OTPEntity otpEntity = new OTPEntity();
        otpEntity.setOtpNumber(newOtp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpEntity.setEmail(email);
        otpEntity.setAttemptRemaining(MAX_ATTEMPTS);

        otpRepository.save(otpEntity);
        sendOtpMail(email, newOtp);

        return newOtp;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OTPEntity> otpEntityOpt = otpRepository.findByEmail(email);

        if (otpEntityOpt.isEmpty()) return false;

        OTPEntity otpEntity = otpEntityOpt.get();

        // Check if the OTP is expired
        if (otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            return false;
        }

        // Check if OTP matches
        if (!otpEntity.getOtpNumber().equals(otp)) {
            // Reduce attempts
            int remainingAttempts = otpEntity.getAttemptRemaining() - 1;
            otpEntity.setAttemptRemaining(remainingAttempts);

            // If attempts are exhausted, delete OTP
            if (remainingAttempts <= 0) {
                otpRepository.delete(otpEntity);
            } else {
                otpRepository.save(otpEntity);
            }

            return false;
        }

        // OTP is valid; delete it after verification
        otpRepository.delete(otpEntity);
        return true;
    }
}
package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.MiscellaneousException;
import com.kdu.hufflepuff.ibe.exception.OTPException;
import com.kdu.hufflepuff.ibe.model.entity.OTPEntity;
import com.kdu.hufflepuff.ibe.repository.jpa.OTPRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;
    private static final Random random = new Random();

    private final OTPRepository otpRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOtpMail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("expiry", OTP_EXPIRY_MINUTES);

        String htmlContent = templateEngine.process("otp-email.html", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MiscellaneousException("Failed to send OTP email");
        }
    }

    @Override
    public String generateOtp(String email) {
        String newOtp = String.format("%06d", random.nextInt(999999));

        OTPEntity otpEntity = new OTPEntity();
        otpEntity.setOtpNumber(newOtp);
        otpEntity.setExpirationTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpEntity.setEmail(email);
        otpEntity.setAttemptRemaining(MAX_ATTEMPTS);

        otpRepository.save(otpEntity);
        sendOtpMail(email, newOtp);

        return newOtp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OTPEntity otpEntity = otpRepository.findByEmail(email).orElseThrow(() -> OTPException.invalidEmail(email));

        if (otpEntity.getAttemptRemaining() < 0) {
            otpRepository.delete(otpEntity);
            throw OTPException.maxAttemptsReached(email);
        }

        if (otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            throw OTPException.otpExpired(email);
        }

        if (!otpEntity.getOtpNumber().equals(otp)) {
            otpEntity.setAttemptRemaining(otpEntity.getAttemptRemaining() - 1);
            otpRepository.save(otpEntity);
            throw OTPException.invalidOtp(otp);
        }

        if (otpEntity.isVerified()) {
            throw OTPException.otpAlreadyVerified(email);
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
        return true;
    }

    @Override
    public boolean isOTPVerified(String email, String otp) {
        OTPEntity otpEntity = otpRepository.getOTPEntityByEmailAndOtpNumber(email, otp);
        if (otpEntity == null) {
            return false;
        } else {
            return otpEntity.isVerified();
        }
    }

    @Override
    public void deleteOtp(String otp) {
        otpRepository.deleteByOtpNumber(otp);
    }
}

package com.kdu.hufflepuff.ibe.service.impl;

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
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

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
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    @Override
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

    @Override
    public boolean verifyOtp(String email, String otp) {
        Optional<OTPEntity> otpEntityOpt = otpRepository.findByEmail(email);

        if (otpEntityOpt.isEmpty()) return false;

        OTPEntity otpEntity = otpEntityOpt.get();

        if (otpEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(otpEntity);
            return false;
        }

        if (!otpEntity.getOtpNumber().equals(otp)) {
            int remainingAttempts = otpEntity.getAttemptRemaining() - 1;
            otpEntity.setAttemptRemaining(remainingAttempts);

            if (remainingAttempts <= 0) {
                otpRepository.delete(otpEntity);
            } else {
                otpRepository.save(otpEntity);
            }

            return false;
        }

        otpRepository.delete(otpEntity);
        return true;
    }
}

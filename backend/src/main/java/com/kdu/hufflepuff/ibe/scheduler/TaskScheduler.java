package com.kdu.hufflepuff.ibe.scheduler;

import com.kdu.hufflepuff.ibe.model.entity.PendingEmail;
import com.kdu.hufflepuff.ibe.repository.jpa.PendingEmailRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskScheduler {

    private final PendingEmailRepository pendingEmailRepository;
    private final EmailService emailService;
    @Value("${application.cors.allowed-origins}")
    private String[] frontendAllowedOrigins;

    @Scheduled(fixedRate = 60000, initialDelay = 50000)
    public void sendScheduledEmails() {
        List<PendingEmail> emails = pendingEmailRepository.findBySentFalseAndSendAfterBefore(LocalDateTime.now());

        for (PendingEmail email : emails) {
            try {

                Context context = new Context();
                context.setVariable("guestName", email.getGuestName());
                context.setVariable("bookingId", email.getBookingId());
                context.setVariable("propertyId", email.getPropertyId());
                context.setVariable("reviewUrl", String.format("%s/%s/review/%s",
                        frontendAllowedOrigins[0],
                        email.getTenantId(),
                        email.getBookingId()
                ));

                emailService.sendHtmlEmailWithAttachment(
                        email.getToEmail(),
                        "Your Booking Confirmation",
                        email.getTemplateName(),
                        context,
                        null,
                        null
                );

                email.setSent(true);
                pendingEmailRepository.save(email);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


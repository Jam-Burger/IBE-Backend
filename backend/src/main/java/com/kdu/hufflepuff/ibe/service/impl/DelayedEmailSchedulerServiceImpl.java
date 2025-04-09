package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.entity.PendingEmail;
import com.kdu.hufflepuff.ibe.repository.jpa.PendingEmailRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.DelayedEmailSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DelayedEmailSchedulerServiceImpl implements DelayedEmailSchedulerService {


    private final PendingEmailRepository pendingEmailRepository;

    @Override
    public void scheduleBookingConfirmationEmail(
        String toEmail,
        String guestName,
        Long bookingId,
        Long propertyId,
        Long tenantId
    ) {
        PendingEmail email = new PendingEmail();
        email.setToEmail(toEmail);
        email.setTemplateName("review-template");
        email.setGuestName(guestName);
        email.setBookingId(bookingId);
        email.setPropertyId(propertyId);
        email.setTenantId(tenantId);
        email.setSendAfter(LocalDateTime.now().plusMinutes(1));
        email.setSent(false);

        pendingEmailRepository.save(email);
    }
}


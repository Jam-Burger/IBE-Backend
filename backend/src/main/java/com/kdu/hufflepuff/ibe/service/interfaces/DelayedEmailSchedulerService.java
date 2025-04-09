package com.kdu.hufflepuff.ibe.service.interfaces;

public interface DelayedEmailSchedulerService {
    void scheduleBookingConfirmationEmail(
        String toEmail,
        String guestName,
        Long bookingId,
        Long propertyId,
        Long tenantId
    );
}

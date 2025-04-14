package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PendingEmail extends BaseEntity {
    private String toEmail;
    private String templateName;
    private String guestName;
    private Long bookingId;
    private Long propertyId;
    private Long tenantId;
    private LocalDateTime sendAfter;
    private boolean sent = false;
}


package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPEntity extends BaseEntity {
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp_number", nullable = false)
    private String otpNumber;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "attempt_remaining")
    private Integer attemptRemaining;

    @Column(name = "verified", nullable = false)
    private boolean verified;
}
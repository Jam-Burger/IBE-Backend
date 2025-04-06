package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "booking_extension")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingExtension extends BaseEntity {
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private GuestExtension guestDetails;
} 
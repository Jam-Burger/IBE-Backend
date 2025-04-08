package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_extension")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingExtension extends BaseEntity {
    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false, unique = true, referencedColumnName = "transaction_id")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false, referencedColumnName = "guest_id")
    private GuestExtension guestDetails;

    @ManyToOne
    @JoinColumn(name = "special_offer_id")
    private SpecialOffer specialOffer;
}
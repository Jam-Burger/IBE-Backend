package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "guest_extension")
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuestExtension extends BaseEntity {
    @Column(name = "guest_id", unique = true, nullable = false)
    private Long guestId;

    @Column(name = "billing_first_name", nullable = false)
    private String billingFirstName;

    @Column(name = "billing_last_name", nullable = false)
    private String billingLastName;

    @Column(name = "billing_email", nullable = false, unique = true)
    private String billingEmail;

    @Column(name = "billing_phone", nullable = false)
    private String billingPhone;

    @Column(name = "billing_mailing_address_1", nullable = false)
    private String billingAddress1;

    @Column(name = "billing_mailing_address_2")
    private String billingAddress2;

    @Column(name = "billing_city", nullable = false)
    private String billingCity;

    @Column(name = "billing_state", nullable = false)
    private String billingState;

    @Column(name = "billing_country", nullable = false)
    private String billingCountry;

    @Column(name = "billing_zip", nullable = false)
    private String billingZip;

    @Column(name = "traveler_first_name", nullable = false)
    private String travelerFirstName;

    @Column(name = "traveler_last_name", nullable = false)
    private String travelerLastName;

    @Column(name = "traveler_email", nullable = false)
    private String travelerEmail;

    @Column(name = "traveler_phone", nullable = false)
    private String travelerPhone;

    @Column(name = "special_offers_consent", nullable = false)
    private Boolean receiveOffers;

    @Column(name = "terms_accepted", nullable = false)
    private Boolean agreedToTerms;
} 
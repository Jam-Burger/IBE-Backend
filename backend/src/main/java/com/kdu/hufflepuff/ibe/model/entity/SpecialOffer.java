package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "special_offer")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOffer extends BaseEntity {
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "promo_code")
    private String promoCode;
}
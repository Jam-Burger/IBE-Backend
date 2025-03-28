package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public abstract class Offer extends BaseEntity {
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

    @Column(name = "description")
    private String description;
}

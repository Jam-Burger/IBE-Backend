package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "special_discount")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private LocalDate discountDate;

    @Column(nullable = false)
    private Double discountPercentage;

    @Column
    private String description;
}
package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "special_discount")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
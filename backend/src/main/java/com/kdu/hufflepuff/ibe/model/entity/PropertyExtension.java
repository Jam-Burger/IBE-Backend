package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "property_extension")
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyExtension extends BaseEntity {
    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "availability", columnDefinition = "TEXT")
    private String availability;

    @Column(name = "country")
    private String country;

    @Column(name = "surcharge")
    private Double surcharge;

    @Column(name = "fees", nullable = false)
    private Double fees;

    @Column(name = "tax", nullable = false)
    private Double tax;

    @Column(name = "terms_and_conditions", nullable = false)
    private String termsAndConditions;
} 
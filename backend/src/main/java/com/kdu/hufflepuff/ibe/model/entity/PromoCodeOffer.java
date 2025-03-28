package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "promo_code_offer")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeOffer extends Offer {
    @Column(name = "code", unique = true, nullable = false)
    private String code;
}

package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "special_offer")
@Builder
@Getter
@ToString
@AllArgsConstructor
public class SpecialOffer extends Offer {
}
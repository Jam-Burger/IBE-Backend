package com.kdu.hufflepuff.ibe.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    private Long promotionId;
    private Double priceFactor;
    private String promotionTitle;
    private String promotionDescription;
    private Boolean isDeactivated;
    private Integer minimumDaysOfStay;
    private List<Booking> bookings;
} 
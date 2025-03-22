package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("promotion_id")
    private Long promotionId;
    
    @JsonProperty("price_factor")
    private Double priceFactor;
    
    @JsonProperty("promotion_title")
    private String promotionTitle;
    
    @JsonProperty("promotion_description")
    private String promotionDescription;
    
    @JsonProperty("is_deactivated")
    private Boolean isDeactivated;
    
    @JsonProperty("minimum_days_of_stay")
    private Integer minimumDaysOfStay;
    
    private List<Booking> bookings;
} 
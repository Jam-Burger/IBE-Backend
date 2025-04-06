package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDetailsDTO {
    private Long propertyId;
    private String propertyName;
    private String propertyAddress;
    private String contactNumber;
    private Long tenantId;
    private String availability;
    private String country;
    private Double surcharge;
    private Double fees;
    private String termsAndConditions;
} 
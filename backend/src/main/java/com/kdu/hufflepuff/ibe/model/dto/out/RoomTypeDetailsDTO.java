package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDetailsDTO {
    private Long roomTypeId;
    private String roomTypeName;
    private Integer maxCapacity;
    private Integer areaInSquareFeet;
    private Integer singleBed;
    private Integer doubleBed;
    private Long propertyId;

    // Extension fields
    private Double rating;
    private Long numberOfReviews;
    private String landmark;
    private String description;

    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @Builder.Default
    private List<String> images = new ArrayList<>();
} 
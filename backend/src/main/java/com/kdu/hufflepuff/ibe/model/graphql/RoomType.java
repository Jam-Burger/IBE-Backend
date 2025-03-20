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
public class RoomType {
    private Long roomTypeId;
    private String roomTypeName;
    private Integer maxCapacity;
    private Integer areaInSquareFeet;
    private Integer singleBed;
    private Integer doubleBed;
    private Property property;
    private Long propertyId;
    private List<RoomRateRoomTypeMapping> roomRates;
    private List<Room> rooms;
} 
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
public class Room {
    private Long roomId;
    private Integer roomNumber;
    private Property property;
    private Long propertyId;
    private RoomType roomType;
    private Long roomTypeId;
    private List<RoomAvailability> roomAvailable;
} 
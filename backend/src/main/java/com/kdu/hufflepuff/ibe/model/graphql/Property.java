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
public class Property {
    private Long propertyId;
    private String propertyName;
    private String propertyAddress;
    private String contactNumber;
    private Tenant tenant;
    private Long tenantId;
    private List<Room> rooms;
    private List<RoomType> roomTypes;
    private List<RoomAvailability> roomAvailabilities;
    private List<Booking> bookings;
} 
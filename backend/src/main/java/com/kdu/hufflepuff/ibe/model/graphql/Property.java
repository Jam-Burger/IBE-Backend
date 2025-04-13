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
public class Property {
    @JsonProperty("property_id")
    private Long propertyId;

    @JsonProperty("property_name")
    private String propertyName;

    @JsonProperty("property_address")
    private String propertyAddress;

    @JsonProperty("contact_number")
    private String contactNumber;

    @JsonProperty("tenant_id")
    private Long tenantId;

    private Tenant tenant;
    private List<Room> rooms;

    @JsonProperty("room_types")
    private List<RoomType> roomTypes;

    @JsonProperty("room_availabilities")
    private List<RoomAvailability> roomAvailabilities;

    private List<Booking> bookings;
} 
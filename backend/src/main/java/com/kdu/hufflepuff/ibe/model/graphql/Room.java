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
public class Room {
    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("room_number")
    private Integer roomNumber;

    @JsonProperty("property_id")
    private Long propertyId;

    @JsonProperty("room_type_id")
    private Long roomTypeId;

    private Property property;

    @JsonProperty("room_type")
    private RoomType roomType;

    @JsonProperty("room_available")
    private List<RoomAvailability> roomAvailable;
} 
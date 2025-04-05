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
public class RoomType {
    @JsonProperty("room_type_id")
    private Long roomTypeId;

    @JsonProperty("room_type_name")
    private String roomTypeName;

    @JsonProperty("max_capacity")
    private Integer maxCapacity;

    @JsonProperty("area_in_square_feet")
    private Integer areaInSquareFeet;

    @JsonProperty("single_bed")
    private Integer singleBed;

    @JsonProperty("double_bed")
    private Integer doubleBed;

    @JsonProperty("property_of")
    private Property property;

    @JsonProperty("room_rates")
    private List<RoomRateRoomTypeMapping> roomRates;

    private List<Room> rooms;
} 
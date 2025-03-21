package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRate {
    @JsonProperty("room_rate_id")
    private Long roomRateId;

    @JsonProperty("basic_nightly_rate")
    private Double basicNightlyRate;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("room_types")
    private List<RoomRateRoomTypeMapping> roomTypes;
} 
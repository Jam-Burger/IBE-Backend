package com.kdu.hufflepuff.ibe.model.graphql;

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
    private Long roomRateId;
    private Integer basicNightlyRate;
    private LocalDate date;
    private List<RoomRateRoomTypeMapping> roomTypes;
} 
package com.kdu.hufflepuff.ibe.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateRoomTypeMapping {
    private Long id;
    private RoomRate roomRate;
    private Long roomRateId;
    private RoomType roomType;
    private Long roomTypeId;
} 
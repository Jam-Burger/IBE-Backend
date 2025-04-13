package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateRoomTypeMapping {
    @JsonProperty("room_rate_room_type_mapping_id")
    private Long roomRateRoomTypeMappingId;

    @JsonProperty("room_rate")
    private RoomRate roomRate;

    @JsonProperty("room_type")
    private RoomType roomType;
}
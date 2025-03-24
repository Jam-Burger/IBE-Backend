package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoomRateRoomTypeMappingTest {

    @Test
    void testGettersAndSetters() {
        // Given
        RoomRateRoomTypeMapping mapping = new RoomRateRoomTypeMapping();
        RoomRate roomRate = new RoomRate();

        // When
        mapping.setRoomRate(roomRate);

        // Then
        assertThat(mapping.getRoomRate()).isEqualTo(roomRate);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RoomRate roomRate1 = new RoomRate();
        roomRate1.setRoomRateId(1L);

        RoomRate roomRate2 = new RoomRate();
        roomRate2.setRoomRateId(2L);

        RoomRateRoomTypeMapping mapping1 = new RoomRateRoomTypeMapping();
        mapping1.setRoomRate(roomRate1);

        RoomRateRoomTypeMapping mapping2 = new RoomRateRoomTypeMapping();
        mapping2.setRoomRate(roomRate1);

        RoomRateRoomTypeMapping mapping3 = new RoomRateRoomTypeMapping();
        mapping3.setRoomRate(roomRate2);

        // Then
        assertThat(mapping1)
            .isEqualTo(mapping2)
            .isNotEqualTo(mapping3);
        assertThat(mapping1.hashCode())
            .isEqualTo(mapping2.hashCode())
            .isNotEqualTo(mapping3.hashCode());
    }
} 
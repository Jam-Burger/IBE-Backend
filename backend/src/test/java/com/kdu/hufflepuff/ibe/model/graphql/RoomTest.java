package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long roomId = 1L;
        Integer roomNumber = 101;
        Long propertyId = 1L;
        Long roomTypeId = 1L;
        Property property = Property.builder().propertyId(propertyId).build();
        RoomType roomType = RoomType.builder().roomTypeId(roomTypeId).build();
        List<RoomAvailability> roomAvailable = Collections.singletonList(
            RoomAvailability.builder().availabilityId(1L).build()
        );

        // When
        Room room = Room.builder()
            .roomId(roomId)
            .roomNumber(roomNumber)
            .propertyId(propertyId)
            .roomTypeId(roomTypeId)
            .property(property)
            .roomType(roomType)
            .roomAvailable(roomAvailable)
            .build();

        // Then
        assertThat(room)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.getRoomId()).isEqualTo(roomId);
                assertThat(r.getRoomNumber()).isEqualTo(roomNumber);
                assertThat(r.getPropertyId()).isEqualTo(propertyId);
                assertThat(r.getRoomTypeId()).isEqualTo(roomTypeId);
                assertThat(r.getProperty()).isEqualTo(property);
                assertThat(r.getRoomType()).isEqualTo(roomType);
                assertThat(r.getRoomAvailable())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomAvailable);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Room room = new Room();

        // Then
        assertThat(room)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.getRoomId()).isNull();
                assertThat(r.getRoomNumber()).isNull();
                assertThat(r.getPropertyId()).isNull();
                assertThat(r.getRoomTypeId()).isNull();
                assertThat(r.getProperty()).isNull();
                assertThat(r.getRoomType()).isNull();
                assertThat(r.getRoomAvailable()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Room room1 = Room.builder()
            .roomId(1L)
            .roomNumber(101)
            .propertyId(1L)
            .roomTypeId(1L)
            .build();

        Room room2 = Room.builder()
            .roomId(1L)
            .roomNumber(101)
            .propertyId(1L)
            .roomTypeId(1L)
            .build();

        Room room3 = Room.builder()
            .roomId(2L)
            .roomNumber(102)
            .propertyId(1L)
            .roomTypeId(2L)
            .build();

        // Then
        assertThat(room1)
            .isEqualTo(room2)    // Equal objects
            .isNotEqualTo(room3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(room1.hashCode())
            .isEqualTo(room2.hashCode())
            .isNotEqualTo(room3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Room room = Room.builder()
            .roomId(1L)
            .roomNumber(101)
            .propertyId(1L)
            .roomTypeId(1L)
            .build();

        // Then
        assertThat(room.toString())
            .isNotNull()
            .contains(
                "roomId=1",
                "roomNumber=101",
                "propertyId=1",
                "roomTypeId=1"
            );
    }

    @Test
    void testSetters() {
        // Given
        Room room = new Room();
        Long roomId = 1L;
        Integer roomNumber = 101;
        Long propertyId = 1L;
        Long roomTypeId = 1L;
        Property property = Property.builder().propertyId(propertyId).build();
        RoomType roomType = RoomType.builder().roomTypeId(roomTypeId).build();
        List<RoomAvailability> roomAvailable = Collections.singletonList(
            RoomAvailability.builder().availabilityId(1L).build()
        );

        // When
        room.setRoomId(roomId);
        room.setRoomNumber(roomNumber);
        room.setPropertyId(propertyId);
        room.setRoomTypeId(roomTypeId);
        room.setProperty(property);
        room.setRoomType(roomType);
        room.setRoomAvailable(roomAvailable);

        // Then
        assertThat(room)
            .satisfies(r -> {
                assertThat(r.getRoomId()).isEqualTo(roomId);
                assertThat(r.getRoomNumber()).isEqualTo(roomNumber);
                assertThat(r.getPropertyId()).isEqualTo(propertyId);
                assertThat(r.getRoomTypeId()).isEqualTo(roomTypeId);
                assertThat(r.getProperty()).isEqualTo(property);
                assertThat(r.getRoomType()).isEqualTo(roomType);
                assertThat(r.getRoomAvailable()).isEqualTo(roomAvailable);
            });
    }
} 
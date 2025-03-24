package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTypeTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long roomTypeId = 1L;
        String roomTypeName = "Deluxe Room";
        Integer maxCapacity = 4;
        Integer areaInSquareFeet = 400;
        Integer singleBed = 2;
        Integer doubleBed = 1;
        Property property = Property.builder().propertyId(1L).build();
        List<RoomRateRoomTypeMapping> roomRates = Collections.singletonList(
            RoomRateRoomTypeMapping.builder().roomRateRoomTypeMappingId(1L).build()
        );
        List<Room> rooms = Collections.singletonList(
            Room.builder().roomId(1L).build()
        );

        // When
        RoomType roomType = RoomType.builder()
            .roomTypeId(roomTypeId)
            .roomTypeName(roomTypeName)
            .maxCapacity(maxCapacity)
            .areaInSquareFeet(areaInSquareFeet)
            .singleBed(singleBed)
            .doubleBed(doubleBed)
            .property(property)
            .roomRates(roomRates)
            .rooms(rooms)
            .build();

        // Then
        assertThat(roomType)
            .isNotNull()
            .satisfies(type -> {
                assertThat(type.getRoomTypeId()).isEqualTo(roomTypeId);
                assertThat(type.getRoomTypeName()).isEqualTo(roomTypeName);
                assertThat(type.getMaxCapacity()).isEqualTo(maxCapacity);
                assertThat(type.getAreaInSquareFeet()).isEqualTo(areaInSquareFeet);
                assertThat(type.getSingleBed()).isEqualTo(singleBed);
                assertThat(type.getDoubleBed()).isEqualTo(doubleBed);
                assertThat(type.getProperty()).isEqualTo(property);
                assertThat(type.getRoomRates())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomRates);
                assertThat(type.getRooms())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(rooms);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RoomType roomType = new RoomType();

        // Then
        assertThat(roomType)
            .isNotNull()
            .satisfies(type -> {
                assertThat(type.getRoomTypeId()).isNull();
                assertThat(type.getRoomTypeName()).isNull();
                assertThat(type.getMaxCapacity()).isNull();
                assertThat(type.getAreaInSquareFeet()).isNull();
                assertThat(type.getSingleBed()).isNull();
                assertThat(type.getDoubleBed()).isNull();
                assertThat(type.getProperty()).isNull();
                assertThat(type.getRoomRates()).isNull();
                assertThat(type.getRooms()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RoomType roomType1 = RoomType.builder()
            .roomTypeId(1L)
            .roomTypeName("Deluxe Room")
            .maxCapacity(4)
            .build();

        RoomType roomType2 = RoomType.builder()
            .roomTypeId(1L)
            .roomTypeName("Deluxe Room")
            .maxCapacity(4)
            .build();

        RoomType roomType3 = RoomType.builder()
            .roomTypeId(2L)
            .roomTypeName("Suite")
            .maxCapacity(6)
            .build();

        // Then
        assertThat(roomType1)
            .isEqualTo(roomType2)    // Equal objects
            .isNotEqualTo(roomType3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(roomType1.hashCode())
            .isEqualTo(roomType2.hashCode())
            .isNotEqualTo(roomType3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        RoomType roomType = RoomType.builder()
            .roomTypeId(1L)
            .roomTypeName("Deluxe Room")
            .maxCapacity(4)
            .build();

        // Then
        assertThat(roomType.toString())
            .isNotNull()
            .contains(
                "roomTypeId=1",
                "roomTypeName=Deluxe Room",
                "maxCapacity=4"
            );
    }

    @Test
    void testSetters() {
        // Given
        RoomType roomType = new RoomType();
        Long roomTypeId = 1L;
        String roomTypeName = "Deluxe Room";
        Integer maxCapacity = 4;
        Integer areaInSquareFeet = 400;
        Integer singleBed = 2;
        Integer doubleBed = 1;
        Property property = Property.builder().propertyId(1L).build();
        List<RoomRateRoomTypeMapping> roomRates = Collections.singletonList(
            RoomRateRoomTypeMapping.builder().roomRateRoomTypeMappingId(1L).build()
        );
        List<Room> rooms = Collections.singletonList(
            Room.builder().roomId(1L).build()
        );

        // When
        roomType.setRoomTypeId(roomTypeId);
        roomType.setRoomTypeName(roomTypeName);
        roomType.setMaxCapacity(maxCapacity);
        roomType.setAreaInSquareFeet(areaInSquareFeet);
        roomType.setSingleBed(singleBed);
        roomType.setDoubleBed(doubleBed);
        roomType.setProperty(property);
        roomType.setRoomRates(roomRates);
        roomType.setRooms(rooms);

        // Then
        assertThat(roomType)
            .satisfies(type -> {
                assertThat(type.getRoomTypeId()).isEqualTo(roomTypeId);
                assertThat(type.getRoomTypeName()).isEqualTo(roomTypeName);
                assertThat(type.getMaxCapacity()).isEqualTo(maxCapacity);
                assertThat(type.getAreaInSquareFeet()).isEqualTo(areaInSquareFeet);
                assertThat(type.getSingleBed()).isEqualTo(singleBed);
                assertThat(type.getDoubleBed()).isEqualTo(doubleBed);
                assertThat(type.getProperty()).isEqualTo(property);
                assertThat(type.getRoomRates()).isEqualTo(roomRates);
                assertThat(type.getRooms()).isEqualTo(rooms);
            });
    }
} 
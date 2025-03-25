package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomRateTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long roomRateId = 1L;
        Double basicNightlyRate = 150.0;
        LocalDate date = LocalDate.now();
        List<RoomRateRoomTypeMapping> roomTypes = Collections.singletonList(
            RoomRateRoomTypeMapping.builder().roomRateRoomTypeMappingId(1L).build()
        );

        // When
        RoomRate roomRate = RoomRate.builder()
            .roomRateId(roomRateId)
            .basicNightlyRate(basicNightlyRate)
            .date(date)
            .roomTypes(roomTypes)
            .build();

        // Then
        assertThat(roomRate)
            .isNotNull()
            .satisfies(rate -> {
                assertThat(rate.getRoomRateId()).isEqualTo(roomRateId);
                assertThat(rate.getBasicNightlyRate()).isEqualTo(basicNightlyRate);
                assertThat(rate.getDate()).isEqualTo(date);
                assertThat(rate.getRoomTypes())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomTypes);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RoomRate roomRate = new RoomRate();

        // Then
        assertThat(roomRate)
            .isNotNull()
            .satisfies(rate -> {
                assertThat(rate.getRoomRateId()).isNull();
                assertThat(rate.getBasicNightlyRate()).isNull();
                assertThat(rate.getDate()).isNull();
                assertThat(rate.getRoomTypes()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate date = LocalDate.now();
        RoomRate roomRate1 = RoomRate.builder()
            .roomRateId(1L)
            .basicNightlyRate(150.0)
            .date(date)
            .build();

        RoomRate roomRate2 = RoomRate.builder()
            .roomRateId(1L)
            .basicNightlyRate(150.0)
            .date(date)
            .build();

        RoomRate roomRate3 = RoomRate.builder()
            .roomRateId(2L)
            .basicNightlyRate(200.0)
            .date(date.plusDays(1))
            .build();

        // Then
        assertThat(roomRate1)
            .isEqualTo(roomRate2)    // Equal objects
            .isNotEqualTo(roomRate3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(roomRate1.hashCode())
            .isEqualTo(roomRate2.hashCode())
            .isNotEqualTo(roomRate3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        LocalDate date = LocalDate.now();
        RoomRate roomRate = RoomRate.builder()
            .roomRateId(1L)
            .basicNightlyRate(150.0)
            .date(date)
            .build();

        // Then
        assertThat(roomRate.toString())
            .isNotNull()
            .contains(
                "roomRateId=1",
                "basicNightlyRate=150.0",
                date.toString()
            );
    }

    @Test
    void testSetters() {
        // Given
        RoomRate roomRate = new RoomRate();
        Long roomRateId = 1L;
        Double basicNightlyRate = 150.0;
        LocalDate date = LocalDate.now();
        List<RoomRateRoomTypeMapping> roomTypes = Collections.singletonList(
            RoomRateRoomTypeMapping.builder().roomRateRoomTypeMappingId(1L).build()
        );

        // When
        roomRate.setRoomRateId(roomRateId);
        roomRate.setBasicNightlyRate(basicNightlyRate);
        roomRate.setDate(date);
        roomRate.setRoomTypes(roomTypes);

        // Then
        assertThat(roomRate)
            .satisfies(rate -> {
                assertThat(rate.getRoomRateId()).isEqualTo(roomRateId);
                assertThat(rate.getBasicNightlyRate()).isEqualTo(basicNightlyRate);
                assertThat(rate.getDate()).isEqualTo(date);
                assertThat(rate.getRoomTypes()).isEqualTo(roomTypes);
            });
    }
} 
package com.kdu.hufflepuff.ibe.model.dto.out;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DailyRoomRateDTOTest {

    @Test
    void testBuilder() {
        // Given
        LocalDate date = LocalDate.now();
        Double minimumRate = 100.0;

        // When
        DailyRoomRateDTO dto = DailyRoomRateDTO.builder()
            .date(date)
            .minimumRate(minimumRate)
            .build();

        // Then
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getMinimumRate()).isEqualTo(minimumRate);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate date = LocalDate.now();
        DailyRoomRateDTO dto1 = DailyRoomRateDTO.builder()
            .date(date)
            .minimumRate(100.0)
            .build();

        DailyRoomRateDTO dto2 = DailyRoomRateDTO.builder()
            .date(date)
            .minimumRate(100.0)
            .build();

        DailyRoomRateDTO dto3 = DailyRoomRateDTO.builder()
            .date(date)
            .minimumRate(150.0)
            .build();

        // Then
        assertThat(dto1)
            .isEqualTo(dto2)
            .isNotEqualTo(dto3);
        assertThat(dto1.hashCode())
            .isEqualTo(dto2.hashCode())
            .isNotEqualTo(dto3.hashCode());
    }
}
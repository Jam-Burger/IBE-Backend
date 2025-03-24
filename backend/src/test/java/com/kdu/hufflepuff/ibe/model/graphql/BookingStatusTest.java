package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingStatusTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long statusId = 1L;
        String status = "CONFIRMED";
        Boolean isDeactivated = false;
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.now())
                .build()
        );

        // When
        BookingStatus bookingStatus = BookingStatus.builder()
            .statusId(statusId)
            .status(status)
            .isDeactivated(isDeactivated)
            .bookings(bookings)
            .build();

        // Then
        assertThat(bookingStatus)
            .isNotNull()
            .satisfies(bs -> {
                assertThat(bs.getStatusId()).isEqualTo(statusId);
                assertThat(bs.getStatus()).isEqualTo(status);
                assertThat(bs.getIsDeactivated()).isEqualTo(isDeactivated);
                assertThat(bs.getBookings())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(bookings);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        BookingStatus bookingStatus = new BookingStatus();

        // Then
        assertThat(bookingStatus)
            .isNotNull()
            .satisfies(bs -> {
                assertThat(bs.getStatusId()).isNull();
                assertThat(bs.getStatus()).isNull();
                assertThat(bs.getIsDeactivated()).isNull();
                assertThat(bs.getBookings()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        BookingStatus status1 = BookingStatus.builder()
            .statusId(1L)
            .status("CONFIRMED")
            .isDeactivated(false)
            .build();

        BookingStatus status2 = BookingStatus.builder()
            .statusId(1L)
            .status("CONFIRMED")
            .isDeactivated(false)
            .build();

        BookingStatus status3 = BookingStatus.builder()
            .statusId(2L)
            .status("CANCELLED")
            .isDeactivated(true)
            .build();

        // Then
        assertThat(status1)
            .isEqualTo(status2)    // Equal objects
            .isNotEqualTo(status3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(status1.hashCode())
            .isEqualTo(status2.hashCode())
            .isNotEqualTo(status3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        BookingStatus status = BookingStatus.builder()
            .statusId(1L)
            .status("CONFIRMED")
            .isDeactivated(false)
            .build();

        // Then
        assertThat(status.toString())
            .isNotNull()
            .contains(
                "statusId=1",
                "status=CONFIRMED",
                "isDeactivated=false"
            );
    }

    @Test
    void testSetters() {
        // Given
        BookingStatus bookingStatus = new BookingStatus();
        Long statusId = 1L;
        String status = "CONFIRMED";
        Boolean isDeactivated = false;
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.now())
                .build()
        );

        // When
        bookingStatus.setStatusId(statusId);
        bookingStatus.setStatus(status);
        bookingStatus.setIsDeactivated(isDeactivated);
        bookingStatus.setBookings(bookings);

        // Then
        assertThat(bookingStatus)
            .satisfies(bs -> {
                assertThat(bs.getStatusId()).isEqualTo(statusId);
                assertThat(bs.getStatus()).isEqualTo(status);
                assertThat(bs.getIsDeactivated()).isEqualTo(isDeactivated);
                assertThat(bs.getBookings()).isEqualTo(bookings);
            });
    }
}
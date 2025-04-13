package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GuestTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long guestId = 1L;
        String guestName = "John Doe";
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .build()
        );

        // When
        Guest guest = Guest.builder()
            .guestId(guestId)
            .guestName(guestName)
            .bookings(bookings)
            .build();

        // Then
        assertThat(guest)
            .isNotNull()
            .satisfies(g -> {
                assertThat(g.getGuestId()).isEqualTo(guestId);
                assertThat(g.getGuestName()).isEqualTo(guestName);
                assertThat(g.getBookings())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(bookings);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Guest guest = new Guest();

        // Then
        assertThat(guest)
            .isNotNull()
            .satisfies(g -> {
                assertThat(g.getGuestId()).isNull();
                assertThat(g.getGuestName()).isNull();
                assertThat(g.getBookings()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Guest guest1 = Guest.builder()
            .guestId(1L)
            .guestName("John Doe")
            .build();

        Guest guest2 = Guest.builder()
            .guestId(1L)
            .guestName("John Doe")
            .build();

        Guest guest3 = Guest.builder()
            .guestId(2L)
            .guestName("Jane Smith")
            .build();

        // Then
        assertThat(guest1)
            .isEqualTo(guest2)    // Equal objects
            .isNotEqualTo(guest3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(guest1.hashCode())
            .isEqualTo(guest2.hashCode())
            .isNotEqualTo(guest3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Guest guest = Guest.builder()
            .guestId(1L)
            .guestName("John Doe")
            .build();

        // Then
        assertThat(guest.toString())
            .isNotNull()
            .contains(
                "guestId=1",
                "guestName=John Doe"
            );
    }

    @Test
    void testSetters() {
        // Given
        Guest guest = new Guest();
        Long guestId = 1L;
        String guestName = "John Doe";
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .build()
        );

        // When
        guest.setGuestId(guestId);
        guest.setGuestName(guestName);
        guest.setBookings(bookings);

        // Then
        assertThat(guest)
            .satisfies(g -> {
                assertThat(g.getGuestId()).isEqualTo(guestId);
                assertThat(g.getGuestName()).isEqualTo(guestName);
                assertThat(g.getBookings()).isEqualTo(bookings);
            });
    }
} 
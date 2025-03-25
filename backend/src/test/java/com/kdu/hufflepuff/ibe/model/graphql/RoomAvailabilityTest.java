package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RoomAvailabilityTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long availabilityId = 1L;
        LocalDate date = LocalDate.now();
        Room room = Room.builder().roomId(1L).build();
        Property property = Property.builder().propertyId(1L).build();
        Booking booking = Booking.builder().bookingId(1L).build();

        // When
        RoomAvailability availability = RoomAvailability.builder()
            .availabilityId(availabilityId)
            .date(date)
            .room(room)
            .property(property)
            .booking(booking)
            .build();

        // Then
        assertThat(availability)
            .isNotNull()
            .satisfies(a -> {
                assertThat(a.getAvailabilityId()).isEqualTo(availabilityId);
                assertThat(a.getDate()).isEqualTo(date);
                assertThat(a.getRoom()).isEqualTo(room);
                assertThat(a.getProperty()).isEqualTo(property);
                assertThat(a.getBooking()).isEqualTo(booking);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RoomAvailability availability = new RoomAvailability();

        // Then
        assertThat(availability)
            .isNotNull()
            .satisfies(a -> {
                assertThat(a.getAvailabilityId()).isNull();
                assertThat(a.getDate()).isNull();
                assertThat(a.getRoom()).isNull();
                assertThat(a.getProperty()).isNull();
                assertThat(a.getBooking()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate date = LocalDate.now();
        RoomAvailability availability1 = RoomAvailability.builder()
            .availabilityId(1L)
            .date(date)
            .room(Room.builder().roomId(1L).build())
            .build();

        RoomAvailability availability2 = RoomAvailability.builder()
            .availabilityId(1L)
            .date(date)
            .room(Room.builder().roomId(1L).build())
            .build();

        RoomAvailability availability3 = RoomAvailability.builder()
            .availabilityId(2L)
            .date(date.plusDays(1))
            .room(Room.builder().roomId(2L).build())
            .build();

        // Then
        assertThat(availability1)
            .isEqualTo(availability2)    // Equal objects
            .isNotEqualTo(availability3) // Different objects
            .isNotEqualTo(null)         // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(availability1.hashCode())
            .isEqualTo(availability2.hashCode())
            .isNotEqualTo(availability3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        LocalDate date = LocalDate.now();
        RoomAvailability availability = RoomAvailability.builder()
            .availabilityId(1L)
            .date(date)
            .room(Room.builder().roomId(1L).build())
            .build();

        // Then
        assertThat(availability.toString())
            .isNotNull()
            .contains(
                "availabilityId=1",
                date.toString(),
                "roomId=1"
            );
    }

    @Test
    void testSetters() {
        // Given
        RoomAvailability availability = new RoomAvailability();
        Long availabilityId = 1L;
        LocalDate date = LocalDate.now();
        Room room = Room.builder().roomId(1L).build();
        Property property = Property.builder().propertyId(1L).build();
        Booking booking = Booking.builder().bookingId(1L).build();

        // When
        availability.setAvailabilityId(availabilityId);
        availability.setDate(date);
        availability.setRoom(room);
        availability.setProperty(property);
        availability.setBooking(booking);

        // Then
        assertThat(availability)
            .satisfies(a -> {
                assertThat(a.getAvailabilityId()).isEqualTo(availabilityId);
                assertThat(a.getDate()).isEqualTo(date);
                assertThat(a.getRoom()).isEqualTo(room);
                assertThat(a.getProperty()).isEqualTo(property);
                assertThat(a.getBooking()).isEqualTo(booking);
            });
    }
} 
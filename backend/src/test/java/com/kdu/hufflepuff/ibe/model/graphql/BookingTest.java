package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long bookingId = 1L;
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);
        Integer adultCount = 2;
        Integer childCount = 1;
        Integer totalCost = 500;
        Integer amountDueAtResort = 100;
        Long propertyId = 1L;
        Long statusId = 1L;
        Long guestId = 1L;
        Long promotionId = 1L;
        Property propertyBooked = Property.builder().propertyId(propertyId).build();
        BookingStatus bookingStatus = BookingStatus.builder().statusId(statusId).build();
        Guest guest = Guest.builder().guestId(guestId).build();
        Promotion promotionApplied = Promotion.builder().promotionId(promotionId).build();
        List<RoomAvailability> roomBooked = Collections.singletonList(
            RoomAvailability.builder().availabilityId(1L).build()
        );

        // When
        Booking booking = Booking.builder()
            .bookingId(bookingId)
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .adultCount(adultCount)
            .childCount(childCount)
            .totalCost(totalCost)
            .amountDueAtResort(amountDueAtResort)
            .promotionId(promotionId)
            .propertyBooked(propertyBooked)
            .bookingStatus(bookingStatus)
            .guest(guest)
            .promotionApplied(promotionApplied)
            .roomBooked(roomBooked)
            .build();

        // Then
        assertThat(booking)
            .isNotNull()
            .satisfies(b -> {
                assertThat(b.getBookingId()).isEqualTo(bookingId);
                assertThat(b.getCheckInDate()).isEqualTo(checkInDate);
                assertThat(b.getCheckOutDate()).isEqualTo(checkOutDate);
                assertThat(b.getAdultCount()).isEqualTo(adultCount);
                assertThat(b.getChildCount()).isEqualTo(childCount);
                assertThat(b.getTotalCost()).isEqualTo(totalCost);
                assertThat(b.getAmountDueAtResort()).isEqualTo(amountDueAtResort);
                assertThat(b.getPromotionId()).isEqualTo(promotionId);
                assertThat(b.getPropertyBooked()).isEqualTo(propertyBooked);
                assertThat(b.getBookingStatus()).isEqualTo(bookingStatus);
                assertThat(b.getGuest()).isEqualTo(guest);
                assertThat(b.getPromotionApplied()).isEqualTo(promotionApplied);
                assertThat(b.getRoomBooked())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomBooked);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Booking booking = new Booking();

        // Then
        assertThat(booking)
            .isNotNull()
            .satisfies(b -> {
                assertThat(b.getBookingId()).isNull();
                assertThat(b.getCheckInDate()).isNull();
                assertThat(b.getCheckOutDate()).isNull();
                assertThat(b.getAdultCount()).isNull();
                assertThat(b.getChildCount()).isNull();
                assertThat(b.getTotalCost()).isNull();
                assertThat(b.getAmountDueAtResort()).isNull();
                assertThat(b.getPromotionId()).isNull();
                assertThat(b.getPropertyBooked()).isNull();
                assertThat(b.getBookingStatus()).isNull();
                assertThat(b.getGuest()).isNull();
                assertThat(b.getPromotionApplied()).isNull();
                assertThat(b.getRoomBooked()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);

        Booking booking1 = Booking.builder()
            .bookingId(1L)
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .adultCount(2)
            .build();

        Booking booking2 = Booking.builder()
            .bookingId(1L)
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .adultCount(2)
            .build();

        Booking booking3 = Booking.builder()
            .bookingId(2L)
            .checkInDate(checkInDate.plusDays(1))
            .checkOutDate(checkOutDate.plusDays(1))
            .adultCount(3)
            .build();

        // Then
        assertThat(booking1)
            .isEqualTo(booking2)    // Equal objects
            .isNotEqualTo(booking3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(booking1.hashCode())
            .isEqualTo(booking2.hashCode())
            .isNotEqualTo(booking3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);

        Booking booking = Booking.builder()
            .bookingId(1L)
            .checkInDate(checkInDate)
            .checkOutDate(checkOutDate)
            .adultCount(2)
            .build();

        // Then
        assertThat(booking.toString())
            .isNotNull()
            .contains(
                "bookingId=1",
                checkInDate.toString(),
                checkOutDate.toString(),
                "adultCount=2",
                "propertyId=1"
            );
    }

    @Test
    void testSetters() {
        // Given
        Booking booking = new Booking();
        Long bookingId = 1L;
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);
        Integer adultCount = 2;
        Integer childCount = 1;
        Integer totalCost = 500;
        Integer amountDueAtResort = 100;
        Long propertyId = 1L;
        Long statusId = 1L;
        Long guestId = 1L;
        Long promotionId = 1L;
        Property propertyBooked = Property.builder().propertyId(propertyId).build();
        BookingStatus bookingStatus = BookingStatus.builder().statusId(statusId).build();
        Guest guest = Guest.builder().guestId(guestId).build();
        Promotion promotionApplied = Promotion.builder().promotionId(promotionId).build();
        List<RoomAvailability> roomBooked = Collections.singletonList(
            RoomAvailability.builder().availabilityId(1L).build()
        );

        // When
        booking.setBookingId(bookingId);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setAdultCount(adultCount);
        booking.setChildCount(childCount);
        booking.setTotalCost(totalCost);
        booking.setAmountDueAtResort(amountDueAtResort);
        booking.setPromotionId(promotionId);
        booking.setPropertyBooked(propertyBooked);
        booking.setBookingStatus(bookingStatus);
        booking.setGuest(guest);
        booking.setPromotionApplied(promotionApplied);
        booking.setRoomBooked(roomBooked);

        // Then
        assertThat(booking)
            .satisfies(b -> {
                assertThat(b.getBookingId()).isEqualTo(bookingId);
                assertThat(b.getCheckInDate()).isEqualTo(checkInDate);
                assertThat(b.getCheckOutDate()).isEqualTo(checkOutDate);
                assertThat(b.getAdultCount()).isEqualTo(adultCount);
                assertThat(b.getChildCount()).isEqualTo(childCount);
                assertThat(b.getTotalCost()).isEqualTo(totalCost);
                assertThat(b.getAmountDueAtResort()).isEqualTo(amountDueAtResort);
                assertThat(b.getPromotionId()).isEqualTo(promotionId);
                assertThat(b.getPropertyBooked()).isEqualTo(propertyBooked);
                assertThat(b.getBookingStatus()).isEqualTo(bookingStatus);
                assertThat(b.getGuest()).isEqualTo(guest);
                assertThat(b.getPromotionApplied()).isEqualTo(promotionApplied);
                assertThat(b.getRoomBooked()).isEqualTo(roomBooked);
            });
    }
} 
package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Booking;

public interface BookingService {
    /**
     * Creates a new booking and locks the room dates
     *
     * @param bookingRequest the booking request details
     * @return the created booking
     */
    Booking createBooking(BookingRequestDTO bookingRequest);

    /**
     * Retrieves a booking by its ID
     *
     * @param bookingId the ID of the booking
     * @return the booking if found
     */
    Booking getBooking(Long bookingId);

    /**
     * Cancels a booking and releases the room date locks
     *
     * @param bookingId the ID of the booking to cancel
     * @return the updated booking
     */
    Booking cancelBooking(Long bookingId);
} 
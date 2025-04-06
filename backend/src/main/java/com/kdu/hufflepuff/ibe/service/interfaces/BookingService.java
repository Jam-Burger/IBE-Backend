package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;

public interface BookingService {
    /**
     * Creates a new booking and locks the room dates
     *
     * @param bookingRequest the booking request details
     * @return the created booking with detailed information
     */
    BookingDetailsDTO createBooking(BookingRequestDTO bookingRequest);

    /**
     * Cancels a booking and releases the room date locks
     *
     * @param bookingId the ID of the booking to cancel
     * @return the updated booking with detailed information
     */
    BookingDetailsDTO cancelBooking(Long bookingId);

    /**
     * Fetches booking details with combined data from GraphQL and database
     *
     * @param bookingId the ID of the booking to fetch
     * @return the booking details with combined information
     */
    BookingDetailsDTO getBookingDetailsById(Long bookingId);
} 
package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityService {
    List<RoomAvailability> fetchAvailableRoomsByPropertyId(Long propertyId, LocalDate startDate, LocalDate endDate);

    List<RoomAvailability> fetchAvailableRoomsByRoomTypeId(Long roomTypeId, LocalDate startDate, LocalDate endDate);

    /**
     * Updates a room availability to connect it to a booking
     *
     * @param availabilityId the ID of the availability to update
     * @param bookingId      the ID of the booking to connect
     * @return the updated RoomAvailability
     */
    RoomAvailability updateRoomAvailability(Long availabilityId, Long bookingId);
} 
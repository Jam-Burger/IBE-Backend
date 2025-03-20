package com.kdu.hufflepuff.ibe.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailability {
    private Long availabilityId;
    private LocalDate date;
    private Room room;
    private Long roomId;
    private Property property;
    private Long propertyId;
    private Booking booking;
    private Long bookingId;
} 
package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

public interface RoomAvailabilityService {
    List<RoomAvailability> fetchAvailableRooms(Long propertyId, LocalDate startDate, LocalDate endDate);
} 
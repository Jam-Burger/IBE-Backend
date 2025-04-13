package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomRateDetailsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomRateService {
    List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);

    /**
     * Get all room rates with their details for each room type between the specified dates
     *
     * @param propertyId The property ID
     * @param startDate  The start date
     * @param endDate    The end date
     * @return List of room rate details containing date, room type ID, and price
     */
    List<RoomRateDetailsDTO> getAllRoomRates(Long propertyId, LocalDate startDate, LocalDate endDate);

    /**
     * Get room rates grouped by room type between the specified dates
     *
     * @param propertyId The property ID
     * @param startDate  The start date
     * @param endDate    The end date
     * @return Map of room type IDs to their list of room rates
     */
    Map<Long, List<RoomRateDetailsDTO>> getRoomRatesByRoomType(Long propertyId, LocalDate startDate, LocalDate endDate);
}
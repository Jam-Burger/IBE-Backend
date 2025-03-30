package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomRateService {
    List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);

    /**
     * Get the average price per night for each room type between the specified dates
     *
     * @param propertyId The property ID
     * @param startDate  The start date
     * @param endDate    The end date
     * @return Map of room type IDs to their average nightly rate
     */
    Map<Long, Double> getAveragePricesByRoomType(Long propertyId, LocalDate startDate, LocalDate endDate);
} 
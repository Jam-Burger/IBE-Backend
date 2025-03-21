package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.DailyRoomRateDTO;
import java.time.LocalDate;
import java.util.List;

public interface RoomRateService {
    List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);
} 
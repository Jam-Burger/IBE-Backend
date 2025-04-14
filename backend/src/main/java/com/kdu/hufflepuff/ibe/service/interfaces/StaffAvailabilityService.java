package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.StaffAvailabilityDTO;

import java.time.LocalDate;
import java.util.List;

public interface StaffAvailabilityService {
    List<StaffAvailabilityDTO> getAvailabilityByStaffId(Long staffId);

    StaffAvailabilityDTO getAvailabilityByStaffIdAndDate(Long staffId, LocalDate date);

    StaffAvailabilityDTO updateAvailability(StaffAvailabilityDTO availabilityDto);
}
package com.kdu.hufflepuff.ibe.service.impl;


import com.kdu.hufflepuff.ibe.exception.AuthException;
import com.kdu.hufflepuff.ibe.model.dto.in.StaffAvailabilityDTO;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability.StaffAvailabilityId;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffAvailabilityRepository;
import com.kdu.hufflepuff.ibe.security.AuthUserService;
import com.kdu.hufflepuff.ibe.service.interfaces.StaffAvailabilityService;
import com.kdu.hufflepuff.ibe.service.interfaces.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StaffAvailabilityServiceImpl implements StaffAvailabilityService {
    private final StaffAvailabilityRepository availabilityRepository;
    private final StaffService staffService;
    private final AuthUserService authUserService;

    public List<StaffAvailabilityDTO> getAvailabilityByStaffId(Long staffId) {
        validateStaffAvailabilityAccess(staffId);

        return availabilityRepository.findByIdStaffId(staffId).stream()
            .map(this::convertToDto)
            .toList();
    }

    public StaffAvailabilityDTO getAvailabilityByStaffIdAndDate(Long staffId, LocalDate date) {
        validateStaffAvailabilityAccess(staffId);

        StaffAvailability availability = availabilityRepository.findById_StaffIdAndId_Date(staffId, date);
        if (availability == null) {
            return null;
        }
        return convertToDto(availability);
    }

    @Transactional
    public StaffAvailabilityDTO updateAvailability(StaffAvailabilityDTO availabilityDto) {
        validateStaffAvailabilityAccess(availabilityDto.getStaffId());

        // Verify staff exists
        if (!staffService.existsById(availabilityDto.getStaffId())) {
            throw new AuthException("Staff does not exist");
        }

        // Create the composite ID
        StaffAvailabilityId availabilityId = new StaffAvailabilityId();
        availabilityId.setStaffId(availabilityDto.getStaffId());
        availabilityId.setDate(availabilityDto.getDate());

        // Find existing or create new
        StaffAvailability availability = availabilityRepository.findById(availabilityId)
            .orElse(new StaffAvailability());

        // Set/update properties
        availability.setId(availabilityId);
        availability.setIsAvailable(availabilityDto.getIsAvailable());

        // Save and return
        StaffAvailability savedAvailability = availabilityRepository.save(availability);
        return convertToDto(savedAvailability);
    }

    /**
     * Validate if the current user has permission to access/modify the availability for the given staff ID
     */
    private void validateStaffAvailabilityAccess(Long staffId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        if (authUserService.isAdmin(email)) {
            return;
        }

        if (authUserService.isStaff(email)) {
            Long currentStaffId = staffService.findByEmail(email).getId();
            if (!Objects.equals(currentStaffId, staffId)) {
                throw new AccessDeniedException("Staff can only access their own availability");
            }
            return;
        }

        throw new AccessDeniedException("Unauthorized access");
    }

    private StaffAvailabilityDTO convertToDto(StaffAvailability availability) {
        return StaffAvailabilityDTO.builder()
            .staffId(availability.getId().getStaffId())
            .date(availability.getId().getDate())
            .isAvailable(availability.getIsAvailable())
            .build();
    }
}

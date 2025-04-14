package com.kdu.hufflepuff.ibe.service.impl;



import com.kdu.hufflepuff.ibe.model.dto.in.StaffAvailabilityDto;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability;
import com.kdu.hufflepuff.ibe.model.entity.StaffAvailability.StaffAvailabilityId;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffAvailabilityRepository;
import com.kdu.hufflepuff.ibe.repository.jpa.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffAvailabilityService {

    private final StaffAvailabilityRepository availabilityRepository;
    private final StaffRepository staffRepository;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Get availability for all staff or just the current staff member based on role
     */
    public List<StaffAvailabilityDto> getAllAvailabilities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // If admin, return all availabilities
        if (userDetailsService.isAdmin(username)) {
            return availabilityRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        // If staff, return only their availabilities
        else if (userDetailsService.isStaff(username)) {
            Long staffId = userDetailsService.getStaffIdFromUsername(username);
            return availabilityRepository.findByIdStaffId(staffId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Unauthorized access");
        }
    }

    /**
     * Get availability by staff ID, respecting permissions
     */
    public List<StaffAvailabilityDto> getAvailabilityByStaffId(Long staffId) {
        validateStaffAvailabilityAccess(staffId);

        return availabilityRepository.findByIdStaffId(staffId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get availability for a specific staff on a specific date
     */
    public StaffAvailabilityDto getAvailabilityByStaffIdAndDate(Long staffId, LocalDate date) {
        validateStaffAvailabilityAccess(staffId);

        StaffAvailability availability = availabilityRepository.findByStaffIdAndDate(staffId, date);
        if (availability == null) {
            return null;
        }
        return convertToDto(availability);
    }

    /**
     * Create or update staff availability
     */
    @Transactional
    public StaffAvailabilityDto updateAvailability(StaffAvailabilityDto availabilityDto) {
        validateStaffAvailabilityAccess(availabilityDto.getStaffId());

        // Verify staff exists
        if (!staffRepository.existsById(availabilityDto.getStaffId())) {
            throw new IllegalArgumentException("Staff not found with ID: " + availabilityDto.getStaffId());
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
     * Delete a staff availability entry
     */
    @Transactional
    public void deleteAvailability(Long staffId, LocalDate date) {
        validateStaffAvailabilityAccess(staffId);

        StaffAvailabilityId availabilityId = new StaffAvailabilityId();
        availabilityId.setStaffId(staffId);
        availabilityId.setDate(date);

        availabilityRepository.deleteById(availabilityId);
    }

    /**
     * Validate if the current user has permission to access/modify the availability for the given staff ID
     */
    private void validateStaffAvailabilityAccess(Long staffId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Admins can access any staff's availability
        if (userDetailsService.isAdmin(username)) {
            return;
        }

        // Staff can only access their own availability
        if (userDetailsService.isStaff(username)) {
            Long currentStaffId = userDetailsService.getStaffIdFromUsername(username);
            if (!Objects.equals(currentStaffId, staffId)) {
                throw new AccessDeniedException("Staff can only access their own availability");
            }
            return;
        }

        throw new AccessDeniedException("Unauthorized access");
    }

    /**
     * Convert from entity to DTO
     */
    private StaffAvailabilityDto convertToDto(StaffAvailability availability) {
        return StaffAvailabilityDto.builder()
                .staffId(availability.getId().getStaffId())
                .date(availability.getId().getDate())
                .isAvailable(availability.getIsAvailable())
                .build();
    }
}

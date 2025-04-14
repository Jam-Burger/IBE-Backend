package com.kdu.hufflepuff.ibe.controller;


import com.kdu.hufflepuff.ibe.model.dto.in.StaffAvailabilityDto;
import com.kdu.hufflepuff.ibe.service.impl.StaffAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/staff-availability")
public class StaffAvailabilityController {

    private final StaffAvailabilityService availabilityService;

    public StaffAvailabilityController(StaffAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    // Use hasAnyAuthority instead
    public ResponseEntity<List<StaffAvailabilityDto>> getAllAvailabilities() {
        log.info("---------------Fetching all availabilities---------------------");
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<StaffAvailabilityDto>> getAvailabilityByStaffId(@PathVariable Long staffId) {
        log.info("Fetching availability for staff ID: {}", staffId);
        return ResponseEntity.ok(availabilityService.getAvailabilityByStaffId(staffId));
    }

    @GetMapping("/staff/{staffId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<StaffAvailabilityDto> getAvailabilityByStaffIdAndDate(
            @PathVariable Long staffId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        StaffAvailabilityDto availability = availabilityService.getAvailabilityByStaffIdAndDate(staffId, date);
        if (availability == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availability);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<StaffAvailabilityDto> createOrUpdateAvailability(@RequestBody StaffAvailabilityDto availabilityDto) {
        StaffAvailabilityDto updatedAvailability = availabilityService.updateAvailability(availabilityDto);
        return new ResponseEntity<>(updatedAvailability, HttpStatus.OK);
    }

    @DeleteMapping("/staff/{staffId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long staffId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        availabilityService.deleteAvailability(staffId, date);
        return ResponseEntity.noContent().build();
    }
}

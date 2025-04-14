package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.StaffAvailabilityDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.StaffAvailabilityService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/{tenantId}/staff-availability")
public class StaffAvailabilityController {
    private final StaffAvailabilityService availabilityService;

    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StaffAvailabilityDTO>>> getAvailabilityByStaffId(@PathVariable Long staffId) {
        List<StaffAvailabilityDTO> availabilityList = availabilityService.getAvailabilityByStaffId(staffId);
        return ApiResponse.<List<StaffAvailabilityDTO>>builder()
            .statusCode(HttpStatus.OK)
            .message("Staff availability retrieved successfully")
            .data(availabilityList)
            .build()
            .send();
    }

    @GetMapping("/{staffId}/date")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<StaffAvailabilityDTO>> getAvailabilityByStaffIdAndDate(
        @PathVariable Long staffId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        StaffAvailabilityDTO availability = availabilityService.getAvailabilityByStaffIdAndDate(staffId, date);
        return ApiResponse.<StaffAvailabilityDTO>builder()
            .statusCode(HttpStatus.OK)
            .message("Staff availability retrieved successfully")
            .data(availability)
            .build()
            .send();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<StaffAvailabilityDTO>> createOrUpdateAvailability(@RequestBody StaffAvailabilityDTO availabilityDTO) {
        StaffAvailabilityDTO updatedAvailability = availabilityService.updateAvailability(availabilityDTO);
        return ApiResponse.<StaffAvailabilityDTO>builder()
            .statusCode(HttpStatus.OK)
            .message("Staff availability updated successfully")
            .data(updatedAvailability)
            .build()
            .send();
    }
}
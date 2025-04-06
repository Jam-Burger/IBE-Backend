package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class RoomAvailabilityController {

    private final RoomAvailabilityService roomAvailabilityService;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableRoomsByProperty(
        @PathVariable Long propertyId,
        @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RoomAvailability> availableRooms = roomAvailabilityService
            .fetchAvailableRoomsByPropertyId(propertyId, startDate, endDate);

        List<Integer> roomNumbers = availableRooms.stream()
            .map(roomAvailability -> roomAvailability.getRoom().getRoomNumber())
            .distinct()
            .toList();

        return ApiResponse.<List<Integer>>builder()
            .message("Available rooms retrieved successfully")
            .data(roomNumbers)
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableRoomsByRoomType(
        @PathVariable Long roomTypeId,
        @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RoomAvailability> availableRooms = roomAvailabilityService
            .fetchAvailableRoomsByRoomTypeId(roomTypeId, startDate, endDate);

        log.info("Available rooms count: {}", availableRooms.getFirst().getRoom());

        List<Integer> roomNumbers = availableRooms.stream()
            .map(roomAvailability -> roomAvailability.getRoom().getRoomNumber())
            .distinct()
            .toList();

        return ApiResponse.<List<Integer>>builder()
            .message("Available rooms retrieved successfully")
            .data(roomNumbers)
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
} 
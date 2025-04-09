package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Room Availability", description = "Endpoints to check available rooms by property or room type")
public class RoomAvailabilityController {

    private final RoomAvailabilityService roomAvailabilityService;

    @Operation(
            summary = "Get available rooms by property ID",
            description = "Returns a list of available room numbers for a specific property within a date range.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved available rooms",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Integer.class))
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    )
            }
    )
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableRoomsByProperty(
            @Parameter(description = "ID of the property", example = "101") @PathVariable Long propertyId,
            @Parameter(description = "Start date in ISO format (yyyy-MM-dd)", example = "2025-04-10")
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date in ISO format (yyyy-MM-dd)", example = "2025-04-15")
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

    @Operation(
            summary = "Get available rooms by room type ID",
            description = "Returns a list of available room numbers for a specific room type within a date range.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved available rooms",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Integer.class))
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    )
            }
    )
    @GetMapping("/room-type/{roomTypeId}")
    public ResponseEntity<ApiResponse<List<Integer>>> getAvailableRoomsByRoomType(
            @Parameter(description = "ID of the room type", example = "5") @PathVariable Long roomTypeId,
            @Parameter(description = "Start date in ISO format (yyyy-MM-dd)", example = "2025-04-10")
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date in ISO format (yyyy-MM-dd)", example = "2025-04-15")
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

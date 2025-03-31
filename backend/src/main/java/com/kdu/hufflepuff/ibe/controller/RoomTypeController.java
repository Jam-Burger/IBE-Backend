package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.RoomTypeFilterDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PaginatedResponseDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.enums.SortOption;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/{tenantId}/{propertyId}")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    @GetMapping("/room-types")
    public ResponseEntity<ApiResponse<List<RoomTypeDetailsDTO>>> getRoomTypesByPropertyId(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId
    ) {
        return ApiResponse.<List<RoomTypeDetailsDTO>>builder()
            .data(roomTypeService.getRoomTypesByPropertyId(tenantId, propertyId))
            .message("Room types retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @GetMapping("/room-types/filter")
    public ResponseEntity<ApiResponse<PaginatedResponseDTO<RoomTypeDetailsDTO>>> filterRoomTypes(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        @RequestParam(required = false) Integer roomCount,
        @RequestParam(required = false) Boolean isAccessible,
        @RequestParam(required = false) Integer totalGuests,
        @RequestParam(required = false) String bedTypes,
        @RequestParam(required = false) String ratings,
        @RequestParam(required = false) String amenities,
        @RequestParam(required = false) Integer roomSizeMin,
        @RequestParam(required = false) Integer roomSizeMax,
        @RequestParam(required = false) SortOption sortBy,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize
    ) {

        List<String> bedTypesList = bedTypes != null
            ? Arrays.stream(bedTypes.split(",")).toList()
            : List.of();

        List<Integer> ratingsList = ratings != null
            ? Arrays.stream(ratings.split(",")).map(Integer::parseInt).toList()
            : List.of();

        List<String> amenitiesList = amenities != null
            ? Arrays.stream(amenities.split(",")).toList()
            : List.of();

        RoomTypeFilterDTO filter = RoomTypeFilterDTO.builder()
            .dateFrom(dateFrom)
            .dateTo(dateTo)
            .roomCount(roomCount)
            .isAccessible(isAccessible)
            .totalGuests(totalGuests)
            .bedTypes(bedTypesList)
            .ratings(ratingsList)
            .amenities(amenitiesList)
            .roomSizeMin(roomSizeMin)
            .roomSizeMax(roomSizeMax)
            .sortBy(sortBy)
            .page(page)
            .pageSize(pageSize)
            .build();

        PaginatedResponseDTO<RoomTypeDetailsDTO> paginatedResponse = roomTypeService.filterRoomTypes(tenantId, propertyId, filter);

        return ApiResponse.<PaginatedResponseDTO<RoomTypeDetailsDTO>>builder()
            .data(paginatedResponse)
            .message("Filtered room types retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @GetMapping("/amenities")
    public ResponseEntity<ApiResponse<List<String>>> getAmenitiesByPropertyId(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId
    ) {
        return ApiResponse.<List<String>>builder()
            .data(roomTypeService.getAmenitiesByPropertyId(tenantId, propertyId))
            .message("Amenities retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
} 
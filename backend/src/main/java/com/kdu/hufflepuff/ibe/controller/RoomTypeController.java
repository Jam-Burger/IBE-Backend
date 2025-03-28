package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/{propertyId}/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    @GetMapping
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
} 
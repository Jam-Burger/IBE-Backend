package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDetailsDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getProperties(@PathVariable Long tenantId) {
        return ApiResponse.<List<PropertyDTO>>builder()
            .data(propertyService.getProperties(tenantId))
            .message("Properties retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDetailsDTO>> getPropertyDetails(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId) {
        return ApiResponse.<PropertyDetailsDTO>builder()
            .data(propertyService.getPropertyDetails(tenantId, propertyId))
            .message("Property details retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
}


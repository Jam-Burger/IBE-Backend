package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDetailsDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Properties", description = "Property information and details API")
public class PropertyController {
    private final PropertyService propertyService;

    @Operation(
            summary = "Get all properties",
            description = "Retrieves a list of all properties for a tenant"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Properties retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No properties found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getProperties(
            @Parameter(description = "ID of the tenant") @PathVariable Long tenantId) {
        return ApiResponse.<List<PropertyDTO>>builder()
                .data(propertyService.getProperties(tenantId))
                .message("Properties retrieved successfully")
                .statusCode(HttpStatus.OK)
                .build()
                .send();
    }

    @Operation(
            summary = "Get property details",
            description = "Retrieves detailed information about a specific property"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Property details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Property not found",
                    content = @Content)
    })
    @GetMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<PropertyDetailsDTO>> getPropertyDetails(
            @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
            @Parameter(description = "ID of the property to retrieve") @PathVariable Long propertyId) {
        return ApiResponse.<PropertyDetailsDTO>builder()
                .data(propertyService.getPropertyDetails(tenantId, propertyId))
                .message("Property details retrieved successfully")
                .statusCode(HttpStatus.OK)
                .build()
                .send();
    }
}
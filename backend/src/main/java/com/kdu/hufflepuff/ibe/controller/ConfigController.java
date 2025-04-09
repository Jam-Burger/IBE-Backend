package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.ConfigResponseDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.*;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.WebsiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/{tenantId}/config")
@RequiredArgsConstructor
@Tag(name = "Configuration", description = "Website configuration management API")
public class ConfigController {

    private final WebsiteConfigService configService;
    private final ModelMapper modelMapper;

    @Operation(
        summary = "Get website configuration",
        description = "Retrieves a specific configuration type for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration retrieved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Configuration not found",
            content = @Content)
    })
    @GetMapping("/{configType}")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> getConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "Type of the configuration") @PathVariable ConfigType configType) {
        WebsiteConfigModel config = configService.getConfig(tenantId, configType);
        return createResponse("Configuration retrieved successfully", config, HttpStatus.OK);
    }

    @Operation(
        summary = "Save global configuration",
        description = "Creates or updates the global configuration for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Global configuration saved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content)
    })
    @PostMapping("/GLOBAL")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveGlobalConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Valid @RequestBody ConfigRequestDTO<GlobalConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.GLOBAL,
            configRequest
        );
        return createResponse("Global configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Save landing page configuration",
        description = "Creates or updates the landing page configuration for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Landing page configuration saved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content)
    })
    @PostMapping("/LANDING")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveLandingConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Valid @RequestBody ConfigRequestDTO<LandingPageConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.LANDING,
            configRequest
        );
        return createResponse("Landing page configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Save rooms list configuration",
        description = "Creates or updates the rooms list configuration for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Rooms list configuration saved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content)
    })
    @PostMapping("/ROOMS_LIST")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveRoomsListConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Valid @RequestBody ConfigRequestDTO<RoomsListConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.ROOMS_LIST,
            configRequest
        );
        return createResponse("Rooms list configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Save checkout configuration",
        description = "Creates or updates the checkout configuration for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Checkout configuration saved successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content)
    })
    @PostMapping("/CHECKOUT")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveCheckoutConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Valid @RequestBody ConfigRequestDTO<CheckoutConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.CHECKOUT,
            configRequest
        );
        return createResponse("Checkout configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Delete website configuration",
        description = "Deletes a specific configuration type for a tenant"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration deleted successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Configuration not found",
            content = @Content)
    })
    @DeleteMapping("/{configType}")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> deleteConfig(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "Type of the configuration") @PathVariable ConfigType configType) {
        WebsiteConfigModel deletedConfig = configService.deleteConfig(tenantId, configType);
        return createResponse("Configuration deleted successfully", deletedConfig, HttpStatus.OK);
    }

    private ResponseEntity<ApiResponse<ConfigResponseDTO>> createResponse(String message, WebsiteConfigModel config, HttpStatusCode statusCode) {
        ConfigResponseDTO response = modelMapper.map(config, ConfigResponseDTO.class);
        if (config.getGlobalConfigModel() != null) {
            response.setConfigData(config.getGlobalConfigModel());
        } else if (config.getLandingPageConfigModel() != null) {
            response.setConfigData(config.getLandingPageConfigModel());
        } else if (config.getRoomsListConfigModel() != null) {
            response.setConfigData(config.getRoomsListConfigModel());
        } else if (config.getCheckoutConfigModel() != null) {
            response.setConfigData(config.getCheckoutConfigModel());
        }

        return com.kdu.hufflepuff.ibe.model.response.ApiResponse.<ConfigResponseDTO>builder()
            .statusCode(statusCode)
            .message(message)
            .data(response)
            .build()
            .send();
    }
}
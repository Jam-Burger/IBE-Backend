package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.ConfigResponseDTO;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.WebsiteConfigService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/config")
@RequiredArgsConstructor
public class ConfigController {
    private final WebsiteConfigService configService;
    private final ModelMapper modelMapper;

    @GetMapping("/{tenantId}/{configType}")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> getConfig(
        @PathVariable String tenantId,
        @PathVariable ConfigType configType) {
        WebsiteConfigModel config = configService.getConfig(tenantId, configType);
        return createResponse("Configuration retrieved successfully", config, HttpStatus.OK);
    }

    @PostMapping("/{tenantId}/GLOBAL")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveGlobalConfig(
        @PathVariable String tenantId,
        @RequestBody ConfigRequestDTO<GlobalConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.GLOBAL,
            configRequest
        );
        return createResponse("Global configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @PostMapping("/{tenantId}/LANDING")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> saveLandingConfig(
        @PathVariable String tenantId,
        @RequestBody ConfigRequestDTO<LandingPageConfigModel> configRequest) {
        WebsiteConfigModel savedConfig = configService.saveConfig(
            tenantId,
            ConfigType.LANDING,
            configRequest
        );
        return createResponse("Landing page configuration saved successfully", savedConfig, HttpStatus.CREATED);
    }

    @DeleteMapping("/{tenantId}/{configType}")
    public ResponseEntity<ApiResponse<ConfigResponseDTO>> deleteConfig(
        @PathVariable String tenantId,
        @PathVariable ConfigType configType) {
        WebsiteConfigModel deletedConfig = configService.deleteConfig(tenantId, configType);
        return createResponse("Configuration deleted successfully", deletedConfig, HttpStatus.OK);
    }

    private ResponseEntity<ApiResponse<ConfigResponseDTO>> createResponse(String message, WebsiteConfigModel config, HttpStatusCode statusCode) {
        ConfigResponseDTO response = modelMapper.map(config, ConfigResponseDTO.class);
        if (config.getGlobalConfigModel() != null) {
            response.setConfigData(config.getGlobalConfigModel());
        } else if (config.getLandingPageConfigModel() != null) {
            response.setConfigData(config.getLandingPageConfigModel());
        }

        return ApiResponse.<ConfigResponseDTO>builder()
            .statusCode(statusCode)
            .message(message)
            .data(response)
            .build()
            .send();
    }
}
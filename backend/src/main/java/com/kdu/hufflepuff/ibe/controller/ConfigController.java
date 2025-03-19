package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfig;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfig;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfig;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.WebsiteConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("api/v1/config")
public class ConfigController {
    private final WebsiteConfigService configService;

    public ConfigController(WebsiteConfigService configService) {
        this.configService = configService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WebsiteConfig>> getConfig(
        @RequestParam String tenantId,
        @RequestParam String configType) {
        Optional<WebsiteConfig> config = configService.getConfig(tenantId, configType);
        if (config.isPresent()) {
            return ApiResponse.<WebsiteConfig>builder()
                .statusCode(HttpStatus.OK)
                .message("Configuration retrieved successfully")
                .data(config.get())
                .build()
                .send();
        } else {
            throw new RuntimeException("Configuration not found");
        }
    }

    @PostMapping(value = "/{tenantId}/global", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> saveGlobalConfig(
        @PathVariable String tenantId,
        @RequestBody ConfigRequestDTO<GlobalConfig> configRequest) {
        configService.saveConfig(tenantId, "GLOBAL", configRequest, GlobalConfig.class);
        return ApiResponse.<Void>builder()
            .statusCode(HttpStatus.OK)
            .message("Global configuration saved successfully")
            .build()
            .send();
    }

    @PostMapping(value = "/{tenantId}/landing", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> saveLandingConfig(
        @PathVariable String tenantId,
        @RequestBody ConfigRequestDTO<LandingPageConfig> configRequest) {
        configService.saveConfig(tenantId, "LANDING", configRequest, LandingPageConfig.class);
        return ApiResponse.<Void>builder()
            .statusCode(HttpStatus.OK)
            .message("Landing page configuration saved successfully")
            .build()
            .send();
    }

    @DeleteMapping(value = "/{tenantId}/{configType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteConfig(
        @PathVariable String tenantId,
        @PathVariable String configType) {
        configService.deleteConfig(tenantId, configType);
        return ApiResponse.<Void>builder()
            .statusCode(HttpStatus.OK)
            .message("Configuration deleted successfully")
            .build()
            .send();
    }
}
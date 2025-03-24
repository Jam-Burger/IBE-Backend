package com.kdu.hufflepuff.ibe.model.dto.out;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigResponseDTOTest {

    @Test
    void testGettersAndSetters() {
        // Given
        ConfigResponseDTO response = new ConfigResponseDTO();
        String tenantId = "tenant123";
        String configType = "GLOBAL";
        Object configData = "test config";
        Long updatedAt = System.currentTimeMillis();

        // When
        response.setTenantId(tenantId);
        response.setConfigType(configType);
        response.setConfigData(configData);
        response.setUpdatedAt(updatedAt);

        // Then
        assertThat(response.getTenantId()).isEqualTo(tenantId);
        assertThat(response.getConfigType()).isEqualTo(configType);
        assertThat(response.getConfigData()).isEqualTo(configData);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void testBuilder() {
        // Given
        String tenantId = "tenant123";
        String configType = "GLOBAL";
        Object configData = "test config";
        Long updatedAt = System.currentTimeMillis();

        // When
        ConfigResponseDTO response = ConfigResponseDTO.builder()
            .tenantId(tenantId)
            .configType(configType)
            .configData(configData)
            .updatedAt(updatedAt)
            .build();

        // Then
        assertThat(response.getTenantId()).isEqualTo(tenantId);
        assertThat(response.getConfigType()).isEqualTo(configType);
        assertThat(response.getConfigData()).isEqualTo(configData);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        String tenantId = "tenant123";
        String configType = "GLOBAL";
        Object configData = "test config";
        Long updatedAt = System.currentTimeMillis();

        ConfigResponseDTO response1 = ConfigResponseDTO.builder()
            .tenantId(tenantId)
            .configType(configType)
            .configData(configData)
            .updatedAt(updatedAt)
            .build();

        ConfigResponseDTO response2 = ConfigResponseDTO.builder()
            .tenantId(tenantId)
            .configType(configType)
            .configData(configData)
            .updatedAt(updatedAt)
            .build();

        ConfigResponseDTO response3 = ConfigResponseDTO.builder()
            .tenantId("different-tenant")
            .configType("LANDING")
            .configData("different config")
            .updatedAt(updatedAt + 1000)
            .build();

        // Then
        assertThat(response1)
            .isEqualTo(response2)
            .isNotEqualTo(response3);
        assertThat(response1.hashCode())
            .isEqualTo(response2.hashCode())
            .isNotEqualTo(response3.hashCode());
    }
} 
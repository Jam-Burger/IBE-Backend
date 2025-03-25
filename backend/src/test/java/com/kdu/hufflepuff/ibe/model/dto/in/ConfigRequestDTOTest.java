package com.kdu.hufflepuff.ibe.model.dto.in;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        // Given
        ConfigRequestDTO<String> request = new ConfigRequestDTO<>();
        String config = "test config";

        // When
        request.setConfig(config);

        // Then
        assertThat(request.getConfig()).isEqualTo(config);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        ConfigRequestDTO<String> request1 = new ConfigRequestDTO<>();
        request1.setConfig("test config");

        ConfigRequestDTO<String> request2 = new ConfigRequestDTO<>();
        request2.setConfig("test config");

        ConfigRequestDTO<String> request3 = new ConfigRequestDTO<>();
        request3.setConfig("different config");

        // Then
        assertThat(request1)
            .isEqualTo(request2)
            .isNotEqualTo(request3);
        assertThat(request1.hashCode())
            .isEqualTo(request2.hashCode())
            .isNotEqualTo(request3.hashCode());
    }
} 
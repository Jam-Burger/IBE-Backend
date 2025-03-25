package com.kdu.hufflepuff.ibe.model.dto.in;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigRequestDTO<T> {
    @Valid
    @JsonUnwrapped
    private T config;
}
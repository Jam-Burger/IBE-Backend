package com.kdu.hufflepuff.ibe.model.dto.in;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigRequestDTO<T> {
    @JsonUnwrapped
    private T config;
}
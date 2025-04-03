package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.PropertyService;
import lombok.RequiredArgsConstructor;
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
    public List<PropertyDTO> getProperties(@PathVariable Long tenantId) {
        return propertyService.getProperties(tenantId);
    }
}


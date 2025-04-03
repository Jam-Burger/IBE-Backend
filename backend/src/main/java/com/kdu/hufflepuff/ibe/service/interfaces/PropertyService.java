package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;

import java.util.List;

public interface PropertyService {
    List<PropertyDTO> getProperties(Long tenantId);
}

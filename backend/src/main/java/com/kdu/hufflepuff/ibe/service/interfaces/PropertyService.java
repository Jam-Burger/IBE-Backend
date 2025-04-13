package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PropertyDetailsDTO;

import java.util.List;

public interface PropertyService {
    List<PropertyDTO> getProperties(Long tenantId);

    /**
     * Get detailed property information including extension data
     *
     * @param tenantId   The tenant ID
     * @param propertyId The property ID
     * @return Detailed property information
     */
    PropertyDetailsDTO getPropertyDetails(Long tenantId, Long propertyId);
}

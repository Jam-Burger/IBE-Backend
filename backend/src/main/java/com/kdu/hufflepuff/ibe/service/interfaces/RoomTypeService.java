package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeDetailsDTO> getRoomTypesByPropertyId(Long tenantId, Long propertyId);
} 
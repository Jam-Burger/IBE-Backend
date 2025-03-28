package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;

import java.time.LocalDate;
import java.util.List;

public interface SpecialDiscountService {
    List<SpecialOffer> getSpecialDiscounts(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);
}

package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;

import java.time.LocalDate;
import java.util.List;

public interface SpecialDiscountService {
    List<SpecialDiscount> getDiscounts(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);
}

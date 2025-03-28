package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialDiscountsRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialDiscountServiceImpl implements SpecialDiscountService {
    private final SpecialDiscountsRepository specialDiscountsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialOffer> getSpecialDiscounts(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        return specialDiscountsRepository.findAllByPropertyIdAndDateRange(propertyId, startDate, endDate);
    }
}

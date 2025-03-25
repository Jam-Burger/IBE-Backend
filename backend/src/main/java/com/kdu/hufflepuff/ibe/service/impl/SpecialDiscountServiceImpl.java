package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
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
    public List<SpecialDiscount> getSpecialDiscounts(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        return specialDiscountsRepository.findAllByPropertyIdAndDiscountDateBetween(propertyId, startDate, endDate);
    }

    @Override
    @Transactional
    public SpecialDiscount createSpecialDiscount(Long tenantId, Long propertyId, CreateSpecialDiscountRequest request) {
        SpecialDiscount specialDiscount = SpecialDiscount.builder()
            .propertyId(propertyId)
            .discountDate(request.getDiscountDate())
            .discountPercentage(request.getDiscountPercentage())
            .build();

        return specialDiscountsRepository.save(specialDiscount);
    }
}

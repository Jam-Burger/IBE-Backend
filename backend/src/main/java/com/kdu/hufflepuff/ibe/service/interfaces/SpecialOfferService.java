package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface SpecialOfferService {
    List<SpecialOfferResponseDTO> getSpecialOffers(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate);

    SpecialOfferResponseDTO getPromoOffer(Long tenantId, Long propertyId, String promoCode, LocalDate startDate, LocalDate endDate);

    List<SpecialOfferResponseDTO> getCalenderOffers(Long tenantId, Long propertyId, LocalDate startDate,
                                                    LocalDate endDate);
}

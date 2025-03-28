package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidPromoCodeException;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialOfferRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialOfferServiceImpl implements SpecialOfferService {
    private final SpecialOfferRepository specialOfferRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialOfferResponseDTO> getSpecialDiscounts(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        List<SpecialOffer> specialOffers = specialOfferRepository.findAllByPropertyIdAndDateRange(propertyId, startDate, endDate);
        return specialOffers.stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialOfferResponseDTO getPromoOffer(Long tenantId, Long propertyId, String promoCode, LocalDate startDate, LocalDate endDate) {

        // Find the promo offer
        SpecialOffer specialOffer = specialOfferRepository.findByPropertyIdAndPromoCode(propertyId, promoCode);
        if (specialOffer == null) {
            throw InvalidPromoCodeException.notFound(promoCode);
        }

        // Validate dates
        LocalDate today = LocalDate.now();
        if (specialOffer.getEndDate().isBefore(today)) {
            throw InvalidPromoCodeException.expired(promoCode);
        }

        // Check if the promo code is applicable for the requested date range
        if (startDate.isAfter(specialOffer.getEndDate()) || endDate.isBefore(specialOffer.getStartDate())) {
            throw InvalidPromoCodeException.notApplicable(promoCode);
        }

        return convertToDTO(specialOffer);
    }

    private SpecialOfferResponseDTO convertToDTO(SpecialOffer specialOffer) {
        return modelMapper.map(specialOffer, SpecialOfferResponseDTO.class);
    }
}

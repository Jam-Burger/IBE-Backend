package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidPromoCodeException;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import com.kdu.hufflepuff.ibe.model.graphql.Promotion;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialOfferRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialOfferServiceImpl implements SpecialOfferService {
    private final SpecialOfferRepository specialOfferRepository;
    private final ModelMapper modelMapper;
    private final GraphQlClient graphQlClient;

    @Override
    @Transactional(readOnly = true)
    public List<SpecialOfferResponseDTO> getSpecialOffers(Long tenantId, Long propertyId, LocalDate startDate,
                                                          LocalDate endDate) {
        List<SpecialOffer> specialOffers = specialOfferRepository.findAllByPropertyIdAndDateRange(propertyId, startDate,
            endDate);

        List<Promotion> promotions = getGraphQLPromotions();

        if (promotions == null) {
            return List.of();
        }

        promotions.forEach(promotion -> {
            SpecialOffer specialOffer = SpecialOffer.builder()
                .propertyId(propertyId)
                .title(promotion.getPromotionTitle())
                .discountPercentage((1 - promotion.getPriceFactor()) * 100)
                .description(promotion.getPromotionDescription())
                .build();
            specialOffers.add(specialOffer);
        });

        return specialOffers.stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialOfferResponseDTO> getCalenderOffers(Long tenantId, Long propertyId, LocalDate startDate,
                                                           LocalDate endDate) {
        List<SpecialOffer> specialOffers = specialOfferRepository.findAllByPropertyIdAndDateRangeWithNoPromoCode(propertyId, startDate,
            endDate);
        return specialOffers.stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialOfferResponseDTO getPromoOffer(Long tenantId, Long propertyId, String promoCode, LocalDate startDate,
                                                 LocalDate endDate) {

        SpecialOffer specialOffer = specialOfferRepository.findByPropertyIdAndPromoCode(propertyId, promoCode);
        if (specialOffer == null) {
            throw InvalidPromoCodeException.notFound(promoCode);
        }

        LocalDate today = LocalDate.now();
        if (specialOffer.getEndDate().isBefore(today)) {
            throw InvalidPromoCodeException.expired(promoCode);
        }

        if (startDate.isAfter(specialOffer.getEndDate()) || endDate.isBefore(specialOffer.getStartDate())) {
            throw InvalidPromoCodeException.notApplicable(promoCode);
        }

        return convertToDTO(specialOffer);
    }

    private SpecialOfferResponseDTO convertToDTO(SpecialOffer specialOffer) {
        return modelMapper.map(specialOffer, SpecialOfferResponseDTO.class);
    }

    private List<Promotion> getGraphQLPromotions() {
        return graphQlClient.document(GraphQLQueries.GET_ALL_PROMOTIONS)
            .retrieve("listPromotions")
            .toEntityList(Promotion.class)
            .block();
    }
}

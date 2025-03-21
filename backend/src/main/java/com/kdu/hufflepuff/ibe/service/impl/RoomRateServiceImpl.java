package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRate;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialDiscountsRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomRateServiceImpl implements RoomRateService {
    private final GraphQlClient graphQlClient;
    private final SpecialDiscountsRepository specialDiscountsRepository;

    @Override
    public List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        // Fetch room rates from GraphQL
        List<RoomRate> graphqlRates = fetchRoomRatesFromGraphQL(propertyId, startDate, endDate);
        
        // Fetch special discounts from JPA
        List<SpecialDiscount> specialDiscounts = specialDiscountsRepository.findAllByPropertyIdAndDiscountDateBetween(propertyId, startDate, endDate);
        
        // Create discount lookup map
        Map<LocalDate, Double> discountMap = specialDiscounts.stream()
                .collect(Collectors.toMap(
                        SpecialDiscount::getDiscountDate,
                        SpecialDiscount::getDiscountPercentage
                ));

        // Combine data and apply discounts
        return graphqlRates.stream()
                .map(rate -> {
                    double discount = discountMap.getOrDefault(rate.getDate(), 0.0);
                    int finalRate = calculateDiscountedRate(rate.getBasicNightlyRate(), discount);
                    
                    return DailyRoomRateDTO.builder()
                            .date(rate.getDate())
                            .minimumRate(finalRate)
                            .build();
                }).toList();
    }

    private List<RoomRate> fetchRoomRatesFromGraphQL(Long propertyId, LocalDate startDate, LocalDate endDate) {
        String query = """
            query getRoomRates($propertyId: Int!, $startDate: Date!, $endDate: Date!) {
                roomRates(propertyId: $propertyId, startDate: $startDate, endDate: $endDate) {
                    date
                    basicNightlyRate
                }
            }
        """;

        return graphQlClient.document(query)
                .variable("propertyId", propertyId)
                .variable("startDate", startDate)
                .variable("endDate", endDate)
                .retrieve("roomRates")
                .toEntityList(RoomRate.class)
                .block();
    }

    private int calculateDiscountedRate(int baseRate, double discountPercentage) {
        return (int) Math.round(baseRate * (1 - discountPercentage / 100));
    }
} 
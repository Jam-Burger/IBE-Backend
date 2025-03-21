package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRate;
import com.kdu.hufflepuff.ibe.repository.jpa.SpecialDiscountsRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

        log.info("GraphQL rates: {}", graphqlRates);
        log.info("Special discounts: {}", specialDiscounts);
        return new ArrayList<>();
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
} 
package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomRateDetailsDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import com.kdu.hufflepuff.ibe.util.DateFormatUtils;
import com.kdu.hufflepuff.ibe.util.DateRangeUtils;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.kdu.hufflepuff.ibe.util.DateRangeUtils.splitDateRange;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomRateServiceImpl implements RoomRateService {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private final GraphQlClient graphQlClient;
    private final SpecialOfferService specialOfferService;
    private final RoomAvailabilityService roomAvailabilityService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "minimumRoomRates", key = "#tenantId + '_' + #propertyId + '_' + #startDate + '_' + #endDate", unless = "#result == null || #result.isEmpty()")
    public List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        log.info("Cache miss - Fetching minimum daily rates for propertyId: {}, startDate: {}, endDate: {}",
            propertyId, startDate, endDate);
        log.info("DATES: {}, {}", DateFormatUtils.toGraphQLDateString(startDate), DateFormatUtils.toGraphQLDateString(endDate));

        CompletableFuture<List<RoomAvailability>> availabilitiesFuture = CompletableFuture.supplyAsync(
            () -> roomAvailabilityService.fetchAvailableRoomsByPropertyId(propertyId, startDate, endDate), EXECUTOR);

        CompletableFuture<List<SpecialOfferResponseDTO>> discountsFuture = CompletableFuture.supplyAsync(
            () -> specialOfferService.getCalenderOffers(tenantId, propertyId, startDate, endDate), EXECUTOR);

        List<RoomAvailability> availabilities = availabilitiesFuture.join();
        List<SpecialOfferResponseDTO> specialOffers = discountsFuture.join();

        if (availabilities.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> availableRoomTypeCounts = availabilities.stream()
            .map(ra -> ra.getRoom().getRoomType().getRoomTypeId())
            .collect(Collectors.groupingBy(
                roomTypeId -> roomTypeId,
                Collectors.counting()
            ));

        CompletableFuture<List<RoomRateRoomTypeMapping>> roomRatesFuture = CompletableFuture.supplyAsync(
            () -> fetchRoomRatesByRoomTypes(availableRoomTypeCounts.keySet().stream().toList(), startDate, endDate), EXECUTOR);

        List<RoomRateRoomTypeMapping> roomRates = roomRatesFuture.join();

        Map<LocalDate, DailyRoomRateDTO> ratesByDate = new HashMap<>();

        roomRates.stream()
            .map(RoomRateRoomTypeMapping::getRoomRate)
            .forEach(rate -> {
                LocalDate date = rate.getDate();
                double currentRate = rate.getBasicNightlyRate();

                Optional<SpecialOfferResponseDTO> applicableOffer = specialOffers.stream()
                    .filter(offer -> (offer.getStartDate().isBefore(date) || offer.getStartDate().isEqual(date)) && (offer.getEndDate().isAfter(date) || offer.getEndDate().isEqual(date)))
                    .max(Comparator.comparingDouble(SpecialOfferResponseDTO::getDiscountPercentage));

                DailyRoomRateDTO dto = ratesByDate.computeIfAbsent(date, k -> DailyRoomRateDTO.builder()
                    .date(k)
                    .minimumRate(currentRate)
                    .discountedRate(currentRate)
                    .build()
                );

                if (currentRate < dto.getMinimumRate()) {
                    dto.setMinimumRate(currentRate);
                }

                if (applicableOffer.isPresent()) {
                    SpecialOfferResponseDTO discount = applicableOffer.get();
                    double discountedRate = currentRate * (1 - (discount.getDiscountPercentage() / 100.0));

                    if (discountedRate < dto.getDiscountedRate()) {
                        dto.setDiscountedRate(discountedRate);
                    }
                }
            });

        return ratesByDate.values().stream()
            .sorted(Comparator.comparing(DailyRoomRateDTO::getDate))
            .toList();
    }

    @Override
    public List<RoomRateDetailsDTO> getAllRoomRates(Long propertyId, LocalDate startDate, LocalDate endDate) {
        List<RoomAvailability> availabilities = roomAvailabilityService.fetchAvailableRoomsByPropertyId(propertyId, startDate, endDate);
        if (availabilities == null || availabilities.isEmpty()) {
            return List.of();
        }

        List<Long> roomTypeIds = availabilities.stream()
            .map(ra -> ra.getRoom().getRoomTypeId())
            .distinct()
            .toList();

        if (roomTypeIds.isEmpty()) {
            return List.of();
        }

        List<RoomRateRoomTypeMapping> rateMappings = fetchRoomRatesByRoomTypes(roomTypeIds, startDate, endDate);

        if (rateMappings == null || rateMappings.isEmpty()) {
            return List.of();
        }

        return rateMappings.stream()
            .filter(mapping -> mapping.getRoomType() != null && mapping.getRoomRate() != null)
            .map(mapping -> RoomRateDetailsDTO.builder()
                .date(mapping.getRoomRate().getDate())
                .roomTypeId(mapping.getRoomType().getRoomTypeId())
                .price(mapping.getRoomRate().getBasicNightlyRate())
                .build())
            .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<RoomRateDetailsDTO>> getRoomRatesByRoomType(Long propertyId, LocalDate startDate, LocalDate endDate) {
        List<RoomRateDetailsDTO> allRates = getAllRoomRates(propertyId, startDate, endDate);

        Map<Long, List<RoomRateDetailsDTO>> ratesByRoomType = new HashMap<>();
        allRates.forEach(rate ->
            ratesByRoomType.computeIfAbsent(rate.getRoomTypeId(), k -> new ArrayList<>())
                .add(rate)
        );

        return ratesByRoomType;
    }

    private List<RoomRateRoomTypeMapping> fetchRoomRatesByRoomTypes(List<Long> roomTypeIds, LocalDate startDate, LocalDate endDate) {
        List<DateRangeUtils.DateRange> dateRanges = splitDateRange(startDate, endDate, 7);

        List<CompletableFuture<List<RoomRateRoomTypeMapping>>> futures = dateRanges.stream()
            .map(dateRange -> CompletableFuture.supplyAsync(() ->
                    graphQlClient.document(GraphQLQueries.GET_ROOM_RATE_MAPPINGS_BY_ROOM_TYPES)
                        .variable("roomTypeIds", roomTypeIds)
                        .variable("startDate", DateFormatUtils.toGraphQLDateString(dateRange.getStart()))
                        .variable("endDate", DateFormatUtils.toGraphQLDateString(dateRange.getEnd()))
                        .retrieve("listRoomRateRoomTypeMappings")
                        .toEntityList(RoomRateRoomTypeMapping.class)
                        .block(),
                EXECUTOR
            ))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .toList();
    }
}
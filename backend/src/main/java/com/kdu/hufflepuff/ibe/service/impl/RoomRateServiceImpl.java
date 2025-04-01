package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Room;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomRateServiceImpl implements RoomRateService {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final GraphQlClient graphQlClient;
    private final SpecialOfferService specialOfferService;

    @Override
    @Transactional(readOnly = true)
    public List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        CompletableFuture<List<RoomAvailability>> availabilitiesFuture = CompletableFuture.supplyAsync(
            () -> fetchAvailableRooms(propertyId, startDate, endDate), EXECUTOR);

        CompletableFuture<List<SpecialOfferResponseDTO>> discountsFuture = CompletableFuture.supplyAsync(
            () -> specialOfferService.getCalenderOffers(tenantId, propertyId, startDate, endDate), EXECUTOR);

        List<RoomAvailability> availabilities = availabilitiesFuture.join();
        List<SpecialOfferResponseDTO> specialOffers = discountsFuture.join();

        if (availabilities.isEmpty()) {
            return List.of();
        }

        List<Long> availableRoomIds = availabilities.stream()
            .map(ra -> ra.getRoom().getRoomId())
            .distinct()
            .toList();

        CompletableFuture<List<Room>> roomsFuture = CompletableFuture.supplyAsync(
            () -> fetchAllRoomRates(availableRoomIds), EXECUTOR);

        List<Room> rooms = roomsFuture.join();

        Map<LocalDate, DailyRoomRateDTO> ratesByDate = new HashMap<>();

        rooms.stream()
            .map(Room::getRoomType)
            .filter(Objects::nonNull)
            .flatMap(roomType -> roomType.getRoomRates().stream())
            .map(RoomRateRoomTypeMapping::getRoomRate)
            .filter(Objects::nonNull)
            .filter(rate -> {
                LocalDate rateDate = rate.getDate();
                return !rateDate.isBefore(startDate) && !rateDate.isAfter(endDate);
            })
            .forEach(rate -> {
                LocalDate date = rate.getDate();
                double currentRate = rate.getBasicNightlyRate();

                Optional<SpecialOfferResponseDTO> applicableOffer = specialOffers.stream()
                    .filter(discount -> !date.isBefore(discount.getStartDate()) && !date.isAfter(discount.getEndDate()))
                    .max(Comparator.comparingDouble(SpecialOfferResponseDTO::getDiscountPercentage));

                DailyRoomRateDTO dto = ratesByDate.computeIfAbsent(date, k -> DailyRoomRateDTO.builder()
                    .date(k)
                    .minimumRate(currentRate)
                    .discountedRate(currentRate)
                    .build());

                if (currentRate < dto.getMinimumRate()) {
                    dto.setMinimumRate(currentRate);
                }

                if (applicableOffer.isPresent()) {
                    log.info("Applicable offer: {}", applicableOffer.get());
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
    @Transactional(readOnly = true)
    public Map<Long, Double> getAveragePricesByRoomType(Long propertyId, LocalDate startDate, LocalDate endDate) {
        final LocalDate effectiveStartDate;
        final LocalDate effectiveEndDate;

        if (startDate == null || endDate == null) {
            effectiveStartDate = LocalDate.now();
            effectiveEndDate = effectiveStartDate.plusDays(1);
        } else {
            effectiveStartDate = startDate;
            effectiveEndDate = endDate;
        }

        List<RoomAvailability> availabilities = fetchAvailableRooms(propertyId, effectiveStartDate, effectiveEndDate);
        if (availabilities == null || availabilities.isEmpty()) {
            return Map.of();
        }

        List<Long> roomTypeIds = availabilities.stream()
            .map(RoomAvailability::getRoom)
            .map(Room::getRoomTypeId)
            .distinct()
            .toList();

        if (roomTypeIds.isEmpty()) {
            return Map.of();
        }

        List<RoomRateRoomTypeMapping> rateMappings = fetchRoomRatesByRoomTypes(roomTypeIds, effectiveStartDate, effectiveEndDate);

        log.info("Get average price by room type: {}", rateMappings);

        Map<Long, List<Double>> ratesByRoomType = new HashMap<>();
        rateMappings.forEach(mapping -> {
            if (mapping.getRoomType() != null && mapping.getRoomRate() != null) {
                Long roomTypeId = mapping.getRoomType().getRoomTypeId();
                Double rate = mapping.getRoomRate().getBasicNightlyRate();

                if (roomTypeId != null && rate != null) {
                    ratesByRoomType.computeIfAbsent(roomTypeId, k -> new ArrayList<>()).add(rate);
                }
            }
        });

        Map<Long, Double> result = new HashMap<>();
        ratesByRoomType.forEach((roomTypeId, rates) -> {
            if (!rates.isEmpty()) {
                double average = rates.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                result.put(roomTypeId, average);
            }
        });

        return result;
    }

    private List<RoomAvailability> fetchAvailableRooms(Long propertyId, LocalDate startDate, LocalDate endDate) {
        return graphQlClient.document(GraphQLQueries.GET_AVAILABLE_ROOMS)
            .variable("propertyId", propertyId)
            .variable("startDate", startDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .variable("endDate", endDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .retrieve("listRoomAvailabilities")
            .toEntityList(RoomAvailability.class)
            .block();
    }

    private List<Room> fetchAllRoomRates(List<Long> availableRoomIds) {
        return graphQlClient.document(GraphQLQueries.GET_ROOMS_BY_IDS)
            .variable("availableRoomIds", availableRoomIds)
            .retrieve("listRooms")
            .toEntityList(Room.class)
            .block();
    }

    private List<RoomRateRoomTypeMapping> fetchRoomRatesByRoomTypes(List<Long> roomTypeIds, LocalDate startDate, LocalDate endDate) {
        return graphQlClient.document(GraphQLQueries.GET_ROOM_RATE_MAPPINGS_BY_ROOM_TYPES)
            .variable("roomTypeIds", roomTypeIds)
            .variable("startDate", startDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .variable("endDate", endDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .retrieve("listRoomRateRoomTypeMappings")
            .toEntityList(RoomRateRoomTypeMapping.class)
            .block();
    }
}
package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Room;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class RoomRateServiceImpl implements RoomRateService {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    private final GraphQlClient graphQlClient;

    @Override
    @Transactional(readOnly = true)
    public List<DailyRoomRateDTO> getMinimumDailyRates(Long tenantId, Long propertyId, LocalDate startDate, LocalDate endDate) {
        CompletableFuture<List<RoomAvailability>> availabilitiesFuture = CompletableFuture.supplyAsync(
            () -> fetchAvailableRooms(propertyId, startDate, endDate), EXECUTOR);

        List<RoomAvailability> availabilities = availabilitiesFuture.join();

        if (availabilities.isEmpty()) {
            return List.of();
        }

        List<Long> availableRoomIds = availabilities.stream()
            .map(ra -> ra.getRoom().getRoomId())
            .distinct()
            .toList();

        // Fetch room rates asynchronously
        CompletableFuture<List<Room>> roomsFuture = CompletableFuture.supplyAsync(
            () -> fetchAllRoomRates(availableRoomIds), EXECUTOR);

        List<Room> rooms = roomsFuture.join();

        Map<LocalDate, Double> minimumRatesByDate = new HashMap<>();

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

                minimumRatesByDate.compute(date, (k, existingMin) ->
                    existingMin == null ? currentRate : Math.min(existingMin, currentRate));
            });

        return minimumRatesByDate.entrySet().stream()
            .map(entry -> DailyRoomRateDTO.builder()
                .date(entry.getKey())
                .minimumRate(entry.getValue())
                .build())
            .sorted(Comparator.comparing(DailyRoomRateDTO::getDate))
            .toList();
    }

    private List<RoomAvailability> fetchAvailableRooms(Long propertyId, LocalDate startDate, LocalDate endDate) {
        String query = """
                query getAvailableRooms($propertyId: Int!, $startDate: AWSDateTime!, $endDate: AWSDateTime!) {
                    listRoomAvailabilities(where: {
                        property: {
                            property_id: {equals: $propertyId}
                        },
                        date: {
                            gte: $startDate,
                            lte: $endDate
                        },
                        booking: {
                            booking_status: {
                                status: {not: {equals: "BOOKED"}}
                            }
                        }
                    }) {
                        availability_id
                        date
                        room {
                            room_id
                            room_type_id
                        }
                    }
                }
            """;

        return graphQlClient.document(query)
            .variable("propertyId", propertyId)
            .variable("startDate", startDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .variable("endDate", endDate.atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
            .retrieve("listRoomAvailabilities")
            .toEntityList(RoomAvailability.class)
            .block();
    }

    private List<Room> fetchAllRoomRates(List<Long> availableRoomIds) {
        String query = """
                query getRooms($availableRoomIds: [Int!]!) {
                    listRooms(where: {
                        room_id: {in: $availableRoomIds}
                    }) {
                        room_id
                        room_number
                        room_type {
                            room_type_id
                            room_type_name
                            max_capacity
                            room_rates {
                                room_rate {
                                    room_rate_id
                                    basic_nightly_rate
                                    date
                                }
                            }
                        }
                    }
                }
            """;

        return graphQlClient.document(query)
            .variable("availableRoomIds", availableRoomIds)
            .retrieve("listRooms")
            .toEntityList(Room.class)
            .block();
    }
}
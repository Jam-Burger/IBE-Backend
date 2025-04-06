package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import com.kdu.hufflepuff.ibe.util.DateRangeUtils;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kdu.hufflepuff.ibe.util.DateRangeUtils.splitDateRange;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private final GraphQlClient graphQlClient;

    public List<RoomAvailability> fetchAvailableRooms(Long propertyId, LocalDate startDate, LocalDate endDate) {
        List<DateRangeUtils.DateRange> dateRanges = splitDateRange(startDate, endDate, 15);

        List<CompletableFuture<List<RoomAvailability>>> futures = dateRanges.stream()
            .map(range -> CompletableFuture.supplyAsync(() ->
                    graphQlClient.document(GraphQLQueries.GET_AVAILABLE_ROOMS)
                        .variable("propertyId", propertyId)
                        .variable("startDate", range.getStart().atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
                        .variable("endDate", range.getEnd().atStartOfDay().atOffset(ZoneOffset.UTC).format(ISO_DATE_FORMATTER))
                        .retrieve("listRoomAvailabilities")
                        .toEntityList(RoomAvailability.class)
                        .block(),
                EXECUTOR
            ))
            .toList();

        // Wait for all futures to complete and combine results
        return futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .toList();
    }
} 
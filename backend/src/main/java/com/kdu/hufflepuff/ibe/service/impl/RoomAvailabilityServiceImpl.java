package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import com.kdu.hufflepuff.ibe.util.DateFormatUtils;
import com.kdu.hufflepuff.ibe.util.DateRangeUtils;
import com.kdu.hufflepuff.ibe.util.GraphQLMutations;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kdu.hufflepuff.ibe.util.DateRangeUtils.splitDateRange;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityServiceImpl implements RoomAvailabilityService {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private final GraphQlClient graphQlClient;

    public List<RoomAvailability> fetchAvailableRoomsByPropertyId(Long propertyId, LocalDate startDate, LocalDate endDate) {
        return fetchAvailableRooms("propertyId", propertyId, startDate, endDate, GraphQLQueries.GET_AVAILABLE_ROOMS_BY_PROPERTY_ID);
    }

    public List<RoomAvailability> fetchAvailableRoomsByRoomTypeId(Long roomTypeId, LocalDate startDate, LocalDate endDate) {
        return fetchAvailableRooms("roomTypeId", roomTypeId, startDate, endDate, GraphQLQueries.GET_AVAILABLE_ROOMS_BY_ROOM_TYPE_ID);
    }

    @Override
    public RoomAvailability updateRoomAvailability(Long availabilityId, Long bookingId) {
        return graphQlClient.document(GraphQLMutations.UPDATE_ROOM_AVAILABILITY)
            .variable("availabilityId", availabilityId)
            .variable("bookingId", bookingId)
            .retrieve("updateRoomAvailability")
            .toEntity(RoomAvailability.class)
            .block();
    }

    private List<RoomAvailability> fetchAvailableRooms(String idName, Long id, LocalDate startDate, LocalDate endDate, String query) {
        List<DateRangeUtils.DateRange> dateRanges = splitDateRange(startDate, endDate, 15);

        List<CompletableFuture<List<RoomAvailability>>> futures = dateRanges.stream()
            .map(range -> CompletableFuture.supplyAsync(() ->
                    graphQlClient.document(query)
                        .variable(idName, id)
                        .variable("startDate", DateFormatUtils.toGraphQLDateString(range.getStart()))
                        .variable("endDate", DateFormatUtils.toGraphQLDateString(range.getEnd()))
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
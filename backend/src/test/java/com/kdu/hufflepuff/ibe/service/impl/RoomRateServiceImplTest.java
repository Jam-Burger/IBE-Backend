package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.graphql.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomRateServiceImplTest {

    @Mock
    private GraphQlClient graphQlClient;

    @Mock
    private GraphQlClient.RequestSpec requestSpec;

    @Mock
    private GraphQlClient.RetrieveSpec responseSpec;

    private RoomRateServiceImpl roomRateService;

    @BeforeEach
    void setUp() {
        roomRateService = new RoomRateServiceImpl(graphQlClient);
    }

    @Test
    void getMinimumDailyRates_ShouldReturnEmptyList_WhenNoAvailableRooms() {
        // Given
        Long tenantId = 1L;
        Long propertyId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(responseSpec);
        when(responseSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(List.of()));

        // When
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(tenantId, propertyId, startDate, endDate);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getMinimumDailyRates_ShouldReturnMinimumRates_WhenRoomsAvailable() {
        // Given
        Long tenantId = 1L;
        Long propertyId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);

        // Mock room availability response
        Room room = new Room();
        room.setRoomId(1L);

        RoomAvailability availability = new RoomAvailability();
        availability.setAvailabilityId(1L);
        availability.setDate(startDate);
        availability.setRoom(room);

        // Mock room rates response
        RoomRate rate1 = new RoomRate();
        rate1.setRoomRateId(1L);
        rate1.setBasicNightlyRate(100.0);
        rate1.setDate(startDate);

        RoomRate rate2 = new RoomRate();
        rate2.setRoomRateId(2L);
        rate2.setBasicNightlyRate(80.0);
        rate2.setDate(startDate);

        RoomRateRoomTypeMapping mapping1 = new RoomRateRoomTypeMapping();
        mapping1.setRoomRate(rate1);

        RoomRateRoomTypeMapping mapping2 = new RoomRateRoomTypeMapping();
        mapping2.setRoomRate(rate2);

        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(1L);
        roomType.setRoomTypeName("Standard");
        roomType.setRoomRates(List.of(mapping1, mapping2));

        Room roomWithRates = new Room();
        roomWithRates.setRoomId(1L);
        roomWithRates.setRoomType(roomType);

        // Mock GraphQL client responses
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve("listRoomAvailabilities"))
            .thenReturn(responseSpec);
        when(responseSpec.toEntityList(RoomAvailability.class))
            .thenReturn(Mono.just(List.of(availability)));

        when(requestSpec.retrieve("listRooms"))
            .thenReturn(responseSpec);
        when(responseSpec.toEntityList(Room.class))
            .thenReturn(Mono.just(List.of(roomWithRates)));

        // When
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(tenantId, propertyId, startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDate()).isEqualTo(startDate);
        assertThat(result.getFirst().getMinimumRate()).isEqualTo(80.0);
    }
} 
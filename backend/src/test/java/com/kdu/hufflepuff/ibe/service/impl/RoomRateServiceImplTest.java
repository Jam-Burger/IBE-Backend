package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.graphql.*;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomRateServiceImplTest {

    private static final Long TENANT_ID = 1L;
    private static final Long PROPERTY_ID = 100L;
    private static final Long ROOM_TYPE_ID = 200L;
    private static final Long ROOM_ID = 300L;
    @Mock
    private GraphQlClient graphQlClient;
    @Mock
    private SpecialOfferService specialOfferService;
    @Mock
    private GraphQlClient.RequestSpec requestSpec;
    @Mock
    private GraphQlClient.RetrieveSpec retrieveSpec;
    private RoomRateServiceImpl roomRateService;

    @BeforeEach
    void setUp() {
        roomRateService = new RoomRateServiceImpl(graphQlClient, specialOfferService);
    }

    @Test
    void getMinimumDailyRates_ShouldReturnEmptyList_WhenNoAvailableRooms() {
        // Prepare
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).isEmpty();
        verify(specialOfferService).getSpecialOffers(TENANT_ID, PROPERTY_ID, startDate, endDate);
    }

    @Test
    void getMinimumDailyRates_ShouldCalculateRatesWithDiscounts() {
        // Prepare
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(2);

        // Mock room availabilities
        Room room = new Room();
        room.setRoomId(ROOM_ID);
        room.setRoomTypeId(ROOM_TYPE_ID);

        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);

        List<RoomAvailability> availabilities = Collections.singletonList(availability);

        // Mock special offers
        SpecialOfferResponseDTO offer = SpecialOfferResponseDTO.builder()
            .propertyId(PROPERTY_ID)
            .discountPercentage(20.0) // 20% discount
            .startDate(today)
            .endDate(endDate)
            .title("Test Offer")
            .build();

        List<SpecialOfferResponseDTO> specialOffers = Collections.singletonList(offer);

        // Mock room type with rates
        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(ROOM_TYPE_ID);

        // Create room rates for each day
        RoomRate rate1 = new RoomRate();
        rate1.setDate(today);
        rate1.setBasicNightlyRate(100.0);

        RoomRate rate2 = new RoomRate();
        rate2.setDate(today.plusDays(1));
        rate2.setBasicNightlyRate(120.0);

        RoomRateRoomTypeMapping mapping1 = new RoomRateRoomTypeMapping();
        mapping1.setRoomType(roomType);
        mapping1.setRoomRate(rate1);

        RoomRateRoomTypeMapping mapping2 = new RoomRateRoomTypeMapping();
        mapping2.setRoomType(roomType);
        mapping2.setRoomRate(rate2);

        List<RoomRateRoomTypeMapping> roomRateMappings = Arrays.asList(mapping1, mapping2);

        // Set up the room with room type that has rates
        room.setRoomType(roomType);
        roomType.setRoomRates(roomRateMappings);

        // Set up mocks
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);

        // Mock room availabilities query
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));

        // Mock room rates query
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));

        // Mock special offers
        when(specialOfferService.getSpecialOffers(TENANT_ID, PROPERTY_ID, today, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, today, endDate);

        // Assert
        assertThat(result).hasSize(2);

        // Check first day
        DailyRoomRateDTO day1 = result.get(0);
        assertThat(day1.getDate()).isEqualTo(today);
        assertThat(day1.getMinimumRate()).isEqualTo(100.0);
        assertThat(day1.getDiscountedRate()).isEqualTo(80.0); // 100 - 20%

        // Check second day
        DailyRoomRateDTO day2 = result.get(1);
        assertThat(day2.getDate()).isEqualTo(today.plusDays(1));
        assertThat(day2.getMinimumRate()).isEqualTo(120.0);
        assertThat(day2.getDiscountedRate()).isEqualTo(96.0); // 120 - 20%
    }

    @Test
    void getAveragePricesByRoomType_ShouldReturnEmptyMap_WhenNoAvailableRooms() {
        // Prepare
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getAveragePricesByRoomType_ShouldReturnEmptyMap_WhenNoDates() {
        // Mock empty availabilities
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, null, null);

        // Assert
        assertThat(result).isEmpty();

        // Verify the effective dates used (should default to today/tomorrow)
        verify(graphQlClient).document(anyString());
    }

    @Test
    void getAveragePricesByRoomType_ShouldCalculateAveragePrices() {
        // Prepare
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(2);

        // Mock room availabilities
        Room room1 = new Room();
        room1.setRoomId(ROOM_ID);
        room1.setRoomTypeId(ROOM_TYPE_ID);

        Room room2 = new Room();
        room2.setRoomId(ROOM_ID + 1);
        room2.setRoomTypeId(ROOM_TYPE_ID + 1);

        RoomAvailability availability1 = new RoomAvailability();
        availability1.setRoom(room1);

        RoomAvailability availability2 = new RoomAvailability();
        availability2.setRoom(room2);

        List<RoomAvailability> availabilities = Arrays.asList(availability1, availability2);

        // Two room types
        RoomType roomType1 = new RoomType();
        roomType1.setRoomTypeId(ROOM_TYPE_ID);

        RoomType roomType2 = new RoomType();
        roomType2.setRoomTypeId(ROOM_TYPE_ID + 1);

        // Room rates for first room type
        RoomRate rate1Day1 = new RoomRate();
        rate1Day1.setDate(today);
        rate1Day1.setBasicNightlyRate(100.0);

        RoomRate rate1Day2 = new RoomRate();
        rate1Day2.setDate(today.plusDays(1));
        rate1Day2.setBasicNightlyRate(120.0);

        // Room rates for second room type
        RoomRate rate2Day1 = new RoomRate();
        rate2Day1.setDate(today);
        rate2Day1.setBasicNightlyRate(150.0);

        RoomRate rate2Day2 = new RoomRate();
        rate2Day2.setDate(today.plusDays(1));
        rate2Day2.setBasicNightlyRate(170.0);

        // Rate mappings for room type 1
        RoomRateRoomTypeMapping mapping1Day1 = new RoomRateRoomTypeMapping();
        mapping1Day1.setRoomType(roomType1);
        mapping1Day1.setRoomRate(rate1Day1);

        RoomRateRoomTypeMapping mapping1Day2 = new RoomRateRoomTypeMapping();
        mapping1Day2.setRoomType(roomType1);
        mapping1Day2.setRoomRate(rate1Day2);

        // Rate mappings for room type 2
        RoomRateRoomTypeMapping mapping2Day1 = new RoomRateRoomTypeMapping();
        mapping2Day1.setRoomType(roomType2);
        mapping2Day1.setRoomRate(rate2Day1);

        RoomRateRoomTypeMapping mapping2Day2 = new RoomRateRoomTypeMapping();
        mapping2Day2.setRoomType(roomType2);
        mapping2Day2.setRoomRate(rate2Day2);

        List<RoomRateRoomTypeMapping> allMappings = Arrays.asList(
            mapping1Day1, mapping1Day2, mapping2Day1, mapping2Day2
        );

        // Set up mocks
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);

        // Mock room availabilities query
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));

        // Mock room rate mappings query
        when(retrieveSpec.toEntityList(RoomRateRoomTypeMapping.class)).thenReturn(Mono.just(allMappings));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, today, endDate);

        // Assert
        assertThat(result).hasSize(2)
            .containsEntry(ROOM_TYPE_ID, 110.0)
            .containsEntry(ROOM_TYPE_ID + 1, 160.0);
    }
} 
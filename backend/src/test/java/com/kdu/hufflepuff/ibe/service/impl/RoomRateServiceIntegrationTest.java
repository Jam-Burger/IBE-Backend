package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Room;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.model.graphql.RoomRate;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = IbeApplication.class)
class RoomRateServiceIntegrationTest {

    private static final Long TENANT_ID = 1L;
    private static final Long PROPERTY_ID = 100L;
    private static final Long ROOM_TYPE_ID = 200L;
    private static final Long ROOM_ID = 300L;

    @Autowired
    private RoomRateServiceImpl roomRateService;

    @MockBean
    private SpecialOfferService specialOfferService;

    @MockBean
    private GraphQlClient graphQlClient;

    @MockBean
    private GraphQlClient.RequestSpec requestSpec;

    @MockBean
    private GraphQlClient.RetrieveSpec retrieveSpec;

    @BeforeEach
    void setUp() {
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
    }

    @Test
    void getMinimumDailyRates_IntegratesWithSpecialOfferService() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(2);

        // Set up room availability data
        Room room = createRoom(ROOM_ID, ROOM_TYPE_ID);
        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);
        List<RoomAvailability> availabilities = Collections.singletonList(availability);

        // Set up special offers from the service
        SpecialOfferResponseDTO offer = SpecialOfferResponseDTO.builder()
            .propertyId(PROPERTY_ID)
            .discountPercentage(20.0)
            .startDate(today)
            .endDate(endDate)
            .title("Integration Test Offer")
            .build();

        List<SpecialOfferResponseDTO> specialOffers = Collections.singletonList(offer);

        // Mock the service responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, today, endDate);

        // Assert
        assertThat(result).hasSize(2);

        // Verify service integration
        verify(specialOfferService).getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate);

        // Check calculation of rates
        DailyRoomRateDTO day1 = result.getFirst();
        assertThat(day1.getDate()).isEqualTo(today);
        assertThat(day1.getMinimumRate()).isEqualTo(100.0);
        assertThat(day1.getDiscountedRate()).isEqualTo(80.0); // 100 - 20%

        DailyRoomRateDTO day2 = result.get(1);
        assertThat(day2.getDate()).isEqualTo(today.plusDays(1));
        assertThat(day2.getMinimumRate()).isEqualTo(120.0);
        assertThat(day2.getDiscountedRate()).isEqualTo(96.0); // 120 - 20%
    }

    @Test
    void getMinimumDailyRates_ReturnsEmptyList_WhenNoAvailableRooms() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(2);

        // Mock empty room availability 
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(Collections.emptyList()));

        // Mock special offers
        List<SpecialOfferResponseDTO> specialOffers = Collections.singletonList(
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(20.0)
                .startDate(today)
                .endDate(endDate)
                .title("Unused Offer")
                .build()
        );
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, today, endDate);

        // Assert
        assertThat(result).isEmpty();

        // Verify service integration
        verify(specialOfferService).getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate);
    }

    @Test
    void getMinimumDailyRates_CalculatesCorrectRates_WhenNoSpecialOffersAvailable() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(2);

        // Set up room availability data
        Room room = createRoom(ROOM_ID, ROOM_TYPE_ID);
        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);
        List<RoomAvailability> availabilities = Collections.singletonList(availability);

        // Mock empty special offers
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate))
            .thenReturn(Collections.emptyList());

        // Mock room availability and rates
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, today, endDate);

        // Assert
        assertThat(result).hasSize(2);

        // Verify service integration
        verify(specialOfferService).getCalenderOffers(TENANT_ID, PROPERTY_ID, today, endDate);

        // Without discounts, minimum rate and discounted rate should be the same
        DailyRoomRateDTO day1 = result.getFirst();
        assertThat(day1.getDate()).isEqualTo(today);
        assertThat(day1.getMinimumRate()).isEqualTo(100.0);
        assertThat(day1.getDiscountedRate()).isEqualTo(100.0); // No discount

        DailyRoomRateDTO day2 = result.get(1);
        assertThat(day2.getDate()).isEqualTo(today.plusDays(1));
        assertThat(day2.getMinimumRate()).isEqualTo(120.0);
        assertThat(day2.getDiscountedRate()).isEqualTo(120.0); // No discount
    }

    // Helper methods
    private Room createRoom(Long roomId, Long roomTypeId) {
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomTypeId(roomTypeId);

        // Set up room type with rates
        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(roomTypeId);

        // Create room rates for each day
        RoomRate rate1 = new RoomRate();
        rate1.setDate(LocalDate.now());
        rate1.setBasicNightlyRate(100.0);

        RoomRate rate2 = new RoomRate();
        rate2.setDate(LocalDate.now().plusDays(1));
        rate2.setBasicNightlyRate(120.0);

        room.setRoomType(roomType);
        roomType.setRoomRates(Arrays.asList(
            createRoomRateMapping(roomType, rate1),
            createRoomRateMapping(roomType, rate2)
        ));

        return room;
    }

    private com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping createRoomRateMapping(
        RoomType roomType, RoomRate roomRate) {
        com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping mapping =
            new com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping();
        mapping.setRoomType(roomType);
        mapping.setRoomRate(roomRate);
        return mapping;
    }
} 
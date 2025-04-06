package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
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

    private com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping createRoomRateMapping(
        RoomType roomType, RoomRate roomRate) {
        com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping mapping =
            new com.kdu.hufflepuff.ibe.model.graphql.RoomRateRoomTypeMapping();
        mapping.setRoomType(roomType);
        mapping.setRoomRate(roomRate);
        return mapping;
    }
} 
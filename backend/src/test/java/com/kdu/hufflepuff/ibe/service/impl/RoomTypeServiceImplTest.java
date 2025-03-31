package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.mapper.RoomTypeMapper;
import com.kdu.hufflepuff.ibe.model.dto.in.RoomTypeFilterDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.PaginatedResponseDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomTypeRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceImplTest {

    private static final Long TENANT_ID = 1L;
    private static final Long PROPERTY_ID = 100L;
    private static final Long ROOM_TYPE_ID = 200L;
    @Mock
    private GraphQlClient graphQlClient;
    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private RoomTypeMapper roomTypeMapper;
    @Mock
    private RoomRateService roomRateService;
    @Mock
    private GraphQlClient.RequestSpec requestSpec;
    @Mock
    private GraphQlClient.RetrieveSpec retrieveSpec;
    @Captor
    private ArgumentCaptor<RoomTypeExtension> roomTypeExtensionCaptor;
    private RoomTypeServiceImpl roomTypeService;

    @BeforeEach
    void setUp() {
        roomTypeService = new RoomTypeServiceImpl(graphQlClient, roomTypeRepository, roomTypeMapper, roomRateService);
    }

    @Test
    void getRoomTypesByPropertyId_ShouldReturnRoomTypes_WhenDataExists() {
        // Prepare
        List<RoomType> roomTypes = Arrays.asList(
            createMockRoomType(ROOM_TYPE_ID),
            createMockRoomType(ROOM_TYPE_ID + 1)
        );
        RoomTypeExtension extension = createMockRoomTypeExtension();
        RoomTypeDetailsDTO mockDto = createMockRoomTypeDetailsDTO(ROOM_TYPE_ID);

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(eq("propertyId"), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomType.class)).thenReturn(Mono.just(roomTypes));

        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(extension));
        when(roomTypeRepository.findById(ROOM_TYPE_ID + 1)).thenReturn(Optional.of(extension));
        when(roomTypeMapper.toDto(any(RoomType.class), any(RoomTypeExtension.class))).thenReturn(mockDto);

        // Act
        List<RoomTypeDetailsDTO> result = roomTypeService.getRoomTypesByPropertyId(TENANT_ID, PROPERTY_ID);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst()).isEqualTo(mockDto);
        verify(roomTypeRepository, times(2)).findById(anyLong());
        verify(roomTypeMapper, times(2)).toDto(any(RoomType.class), any(RoomTypeExtension.class));
    }

    @Test
    void getRoomTypesByPropertyId_ShouldThrowException_WhenRoomTypesNotFound() {
        // Prepare
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(eq("propertyId"), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomType.class)).thenReturn(Mono.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomTypeService.getRoomTypesByPropertyId(TENANT_ID, PROPERTY_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Root types not found for property");
    }

    @Test
    void getRoomTypesByPropertyId_ShouldThrowException_WhenExtensionNotFound() {
        // Prepare
        List<RoomType> roomTypes = Collections.singletonList(createMockRoomType(ROOM_TYPE_ID));

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(eq("propertyId"), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomType.class)).thenReturn(Mono.just(roomTypes));

        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomTypeService.getRoomTypesByPropertyId(TENANT_ID, PROPERTY_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Room type extension not found");
    }

    @Test
    void updateRoomTypeImages_ShouldUpdateImages() {
        // Prepare
        RoomTypeExtension extension = new RoomTypeExtension();
        List<String> imageUrls = Arrays.asList("url1", "url2", "url3");

        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.of(extension));
        when(roomTypeRepository.save(any(RoomTypeExtension.class))).thenReturn(extension);

        // Act
        roomTypeService.updateRoomTypeImages(TENANT_ID, ROOM_TYPE_ID, imageUrls);

        // Assert
        verify(roomTypeRepository).save(roomTypeExtensionCaptor.capture());
        RoomTypeExtension capturedExtension = roomTypeExtensionCaptor.getValue();
        assertThat(capturedExtension.getImages()).isEqualTo(imageUrls);
    }

    @Test
    void updateRoomTypeImages_ShouldThrowException_WhenRoomTypeNotFound() {
        // Prepare
        List<String> imageUrls = Arrays.asList("url1", "url2");
        when(roomTypeRepository.findById(ROOM_TYPE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomTypeService.updateRoomTypeImages(TENANT_ID, ROOM_TYPE_ID, imageUrls))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Room type extension not found");

        verify(roomTypeRepository, never()).save(any());
    }

    @Test
    void filterRoomTypes_ShouldFilterAndSortRoomTypes() {
        // Prepare
        RoomTypeFilterDTO filter = new RoomTypeFilterDTO();
        filter.setPage(0);
        filter.setPageSize(10);
        filter.setDateFrom(LocalDate.now());
        filter.setDateTo(LocalDate.now().plusDays(5));

        RoomTypeDetailsDTO room1 = createMockRoomTypeDetailsDTO(ROOM_TYPE_ID);
        RoomTypeDetailsDTO room2 = createMockRoomTypeDetailsDTO(ROOM_TYPE_ID + 1);

        Map<Long, Double> avgPrices = new HashMap<>();
        avgPrices.put(ROOM_TYPE_ID, 100.0);
        avgPrices.put(ROOM_TYPE_ID + 1, 200.0);

        // Mock the getRoomTypesByPropertyId method
        List<RoomType> mockRoomTypes = Arrays.asList(
            createMockRoomType(ROOM_TYPE_ID),
            createMockRoomType(ROOM_TYPE_ID + 1)
        );
        RoomTypeExtension mockExtension = createMockRoomTypeExtension();

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomType.class)).thenReturn(Mono.just(mockRoomTypes));
        when(roomTypeRepository.findById(anyLong())).thenReturn(Optional.of(mockExtension));
        when(roomTypeMapper.toDto(any(RoomType.class), any(RoomTypeExtension.class))).thenReturn(room1, room2);

        when(roomRateService.getAveragePricesByRoomType(
            PROPERTY_ID, filter.getDateFrom(), filter.getDateTo()
        )).thenReturn(avgPrices);

        // Act
        PaginatedResponseDTO<RoomTypeDetailsDTO> result = roomTypeService.filterRoomTypes(TENANT_ID, PROPERTY_ID, filter);

        // Assert
        assertThat(result.getItems()).hasSize(2);

        // Check that average prices were set
        verify(roomRateService).getAveragePricesByRoomType(
            PROPERTY_ID, filter.getDateFrom(), filter.getDateTo()
        );
    }

    @Test
    void getAmenitiesByPropertyId_ShouldReturnUniqueAmenities() {
        // Prepare
        List<String> amenities1 = Arrays.asList("WiFi", "TV", "Air Conditioning");
        List<String> amenities2 = Arrays.asList("WiFi", "Pool", "Breakfast");

        RoomTypeDetailsDTO room1 = createMockRoomTypeDetailsDTO(ROOM_TYPE_ID);
        room1.setAmenities(amenities1);

        RoomTypeDetailsDTO room2 = createMockRoomTypeDetailsDTO(ROOM_TYPE_ID + 1);
        room2.setAmenities(amenities2);

        List<RoomType> mockRoomTypes = Arrays.asList(
            createMockRoomType(ROOM_TYPE_ID),
            createMockRoomType(ROOM_TYPE_ID + 1)
        );
        RoomTypeExtension mockExtension = createMockRoomTypeExtension();

        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(retrieveSpec.toEntityList(RoomType.class)).thenReturn(Mono.just(mockRoomTypes));
        when(roomTypeRepository.findById(anyLong())).thenReturn(Optional.of(mockExtension));
        when(roomTypeMapper.toDto(any(RoomType.class), any(RoomTypeExtension.class))).thenReturn(room1, room2);

        // Act
        List<String> result = roomTypeService.getAmenitiesByPropertyId(TENANT_ID, PROPERTY_ID);

        // Assert
        assertThat(result)
            .hasSize(5)
            .containsExactlyInAnyOrder("WiFi", "TV", "Air Conditioning", "Pool", "Breakfast");
    }

    private RoomType createMockRoomType(Long roomTypeId) {
        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(roomTypeId);
        return roomType;
    }

    private RoomTypeExtension createMockRoomTypeExtension() {
        RoomTypeExtension extension = new RoomTypeExtension();
        extension.setId(RoomTypeServiceImplTest.ROOM_TYPE_ID);
        extension.setDescription("Test description");
        extension.setImages(Arrays.asList("image1.jpg", "image2.jpg"));
        return extension;
    }

    private RoomTypeDetailsDTO createMockRoomTypeDetailsDTO(Long roomTypeId) {
        RoomTypeDetailsDTO dto = new RoomTypeDetailsDTO();
        dto.setRoomTypeId(roomTypeId);
        dto.setDescription("Test description");
        dto.setImages(Arrays.asList("image1.jpg", "image2.jpg"));
        dto.setAmenities(Arrays.asList("WiFi", "TV"));
        return dto;
    }
} 
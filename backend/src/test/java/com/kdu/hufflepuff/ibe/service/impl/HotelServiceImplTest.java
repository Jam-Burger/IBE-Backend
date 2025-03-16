package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import com.kdu.hufflepuff.ibe.model.entity.HotelTranslation;
import com.kdu.hufflepuff.ibe.repository.HotelRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private List<Hotel> hotels;
    private List<HotelResponseDTO> hotelResponseDTOs;

    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getHeader("Accept-Language")).thenReturn("en");

        // Set up test data
        UUID hotelId = UUID.randomUUID();

        // Create hotel
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName("Test Hotel");
        hotel.setDescription("This is a test hotel");

        // Create hotel translation
        HotelTranslation frTranslation = new HotelTranslation();
        frTranslation.setId(UUID.randomUUID());
        frTranslation.setHotel(hotel);
        frTranslation.setLanguage("fr");
        frTranslation.setTranslatedName("Hôtel de Test");
        frTranslation.setTranslatedDescription("C'est un hôtel de test");

        // Use a List for translations instead of a Set
        List<HotelTranslation> translations = new ArrayList<>();
        translations.add(frTranslation);
        hotel.setTranslations(translations);

        // List of hotels
        hotels = new ArrayList<>();
        hotels.add(hotel);

        // DTO setup
        HotelResponseDTO hotelResponseDTO = new HotelResponseDTO();
        hotelResponseDTO.setId(hotelId);
        hotelResponseDTO.setName("Test Hotel");
        hotelResponseDTO.setDescription("This is a test hotel");

        hotelResponseDTOs = new ArrayList<>();
        hotelResponseDTOs.add(hotelResponseDTO);

        HotelRequestDTO hotelRequestDTO = new HotelRequestDTO();
        hotelRequestDTO.setName("Test Hotel");
        hotelRequestDTO.setDescription("This is a test hotel");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should return all hotels with English language")
    void getAllHotels_WithEnglishLanguage_ShouldReturnAllHotels() {
        // Arrange
        when(hotelRepository.findAll()).thenReturn(hotels);
        when(modelMapper.map(any(Hotel.class), eq(HotelResponseDTO.class))).thenReturn(hotelResponseDTOs.getFirst());

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.getFirst().getName());

        verify(hotelRepository).findAll();
        verify(modelMapper, times(1)).map(any(Hotel.class), eq(HotelResponseDTO.class));
    }

    @Test
    @DisplayName("Should return all hotels with French language translations")
    void getAllHotels_WithFrenchLanguage_ShouldReturnTranslatedHotels() {
        // Arrange
        when(request.getHeader("Accept-Language")).thenReturn("fr");

        // Create a fresh mutable hotel for this test to avoid issues with modifications
        Hotel frenchHotel = new Hotel();
        frenchHotel.setId(UUID.randomUUID());
        frenchHotel.setName("Test Hotel");
        frenchHotel.setDescription("This is a test hotel");

        // Create and set translation
        HotelTranslation frTranslation = new HotelTranslation();
        frTranslation.setLanguage("fr");
        frTranslation.setTranslatedName("Hôtel de Test");
        frTranslation.setTranslatedDescription("C'est un hôtel de test");
        frTranslation.setHotel(frenchHotel);

        // Use a List for translations instead of a Set
        List<HotelTranslation> frTranslations = new ArrayList<>();
        frTranslations.add(frTranslation);
        frenchHotel.setTranslations(frTranslations);

        // Create list of hotels for the repository response
        List<Hotel> frenchHotels = new ArrayList<>();
        frenchHotels.add(frenchHotel);

        when(hotelRepository.findAll()).thenReturn(frenchHotels);

        // Prepare translated response
        HotelResponseDTO translatedResponse = new HotelResponseDTO();
        translatedResponse.setId(frenchHotel.getId());
        translatedResponse.setName("Hôtel de Test");
        translatedResponse.setDescription("C'est un hôtel de test");

        when(modelMapper.map(any(Hotel.class), eq(HotelResponseDTO.class))).thenReturn(translatedResponse);

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hôtel de Test", result.getFirst().getName());
        assertEquals("C'est un hôtel de test", result.getFirst().getDescription());

        verify(hotelRepository).findAll();
        verify(modelMapper, times(1)).map(any(Hotel.class), eq(HotelResponseDTO.class));
    }

    @Test
    @DisplayName("Should handle hotel with name 'Motel'")
    void getAllHotels_WithMotelName_ShouldReturnCorrectly() {
        // Arrange
        Hotel motelHotel = new Hotel();
        motelHotel.setId(UUID.randomUUID());
        motelHotel.setName("Motel");
        motelHotel.setDescription("This is the hotel Motel");
        motelHotel.setTranslations(new ArrayList<>());

        List<Hotel> motelHotels = new ArrayList<>();
        motelHotels.add(motelHotel);

        HotelResponseDTO motelResponseDTO = new HotelResponseDTO();
        motelResponseDTO.setId(motelHotel.getId());
        motelResponseDTO.setName("Motel");
        motelResponseDTO.setDescription("This is the hotel Motel");

        when(hotelRepository.findAll()).thenReturn(motelHotels);
        when(modelMapper.map(any(Hotel.class), eq(HotelResponseDTO.class))).thenReturn(motelResponseDTO);

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Motel", result.getFirst().getName());
        assertEquals("This is the hotel Motel", result.getFirst().getDescription());

        verify(hotelRepository).findAll();
        verify(modelMapper, times(1)).map(any(Hotel.class), eq(HotelResponseDTO.class));
    }
}
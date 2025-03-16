package com.kdu.hufflepuff.ibe.service.impl;
import com.kdu.hufflepuff.ibe.exception.InvalidRequestException;
import com.kdu.hufflepuff.ibe.mapper.HotelMapper;
import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import com.kdu.hufflepuff.ibe.model.entity.HotelTranslation;
import com.kdu.hufflepuff.ibe.repository.HotelRepository;
import com.kdu.hufflepuff.ibe.repository.HotelTranslationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelTranslationRepository translationRepository;

    @Mock
    private TranslationService translationService;

    @Mock
    private HotelMapper hotelMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel hotel;
    private HotelResponseDTO hotelResponseDTO;
    private HotelRequestDTO hotelRequestDTO;
    private List<Hotel> hotels;
    private List<HotelResponseDTO> hotelResponseDTOs;

    @BeforeEach
    void setUp() {
        // Set up test data
        UUID hotelId = UUID.randomUUID();

        // Create hotel
        hotel = new Hotel();
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
        hotelResponseDTO = new HotelResponseDTO();
        hotelResponseDTO.setId(hotelId);
        hotelResponseDTO.setName("Test Hotel");
        hotelResponseDTO.setDescription("This is a test hotel");

        hotelResponseDTOs = new ArrayList<>();
        hotelResponseDTOs.add(hotelResponseDTO);

        hotelRequestDTO = new HotelRequestDTO();
        hotelRequestDTO.setName("Test Hotel");
        hotelRequestDTO.setDescription("This is a test hotel");
    }

    @Test
    @DisplayName("Should return all hotels with English language")
    void getAllHotels_WithEnglishLanguage_ShouldReturnAllHotels() {
        // Arrange
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getHeader("Accept-Language")).thenReturn("en");

        when(hotelRepository.findAll()).thenReturn(hotels);
        when(hotelMapper.toDto(hotels)).thenReturn(hotelResponseDTOs);

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getName());

        verify(hotelRepository).findAll();
        verify(hotelMapper).toDto(hotels);

        // Clean up
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should return all hotels with French language translations")
    void getAllHotels_WithFrenchLanguage_ShouldReturnTranslatedHotels() {
        // Arrange
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
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

        List<HotelResponseDTO> translatedResponses = new ArrayList<>();
        translatedResponses.add(translatedResponse);

        // Mock the mapper to return translated responses
        when(hotelMapper.toDto(any(List.class))).thenReturn(translatedResponses);

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hôtel de Test", result.get(0).getName());
        assertEquals("C'est un hôtel de test", result.get(0).getDescription());

        verify(hotelRepository).findAll();

        // Clean up
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should add a new hotel successfully")
    void addHotel_WithValidData_ShouldAddHotelSuccessfully() {
        // Arrange
        when(hotelMapper.toEntity(any(HotelRequestDTO.class))).thenReturn(hotel);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        when(translationService.translateText(anyString(), eq("en"), eq("fr"))).thenReturn("Translated Text");
        when(hotelMapper.toDto(any(Hotel.class))).thenReturn(hotelResponseDTO);

        // Act
        HotelResponseDTO result = hotelService.addHotel(hotelRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(hotel.getId(), result.getId());
        assertEquals(hotel.getName(), result.getName());

        verify(hotelMapper).toEntity(any(HotelRequestDTO.class));
        verify(hotelRepository).save(any(Hotel.class));
        verify(translationService, times(2)).translateText(anyString(), eq("en"), eq("fr"));
        verify(translationRepository).save(any(HotelTranslation.class));
        verify(hotelMapper).toDto(any(Hotel.class));
    }

    @Test
    @DisplayName("Should throw exception when hotel name is empty")
    void addHotel_WithEmptyName_ShouldThrowException() {
        // Arrange
        hotelRequestDTO.setName("");

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> hotelService.addHotel(hotelRequestDTO));

        verify(hotelRepository, never()).save(any());
        verify(translationService, never()).translateText(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when hotel name is null")
    void addHotel_WithNullName_ShouldThrowException() {
        // Arrange
        hotelRequestDTO.setName(null);

        // Act & Assert
        assertThrows(InvalidRequestException.class, () -> hotelService.addHotel(hotelRequestDTO));

        verify(hotelRepository, never()).save(any());
        verify(translationService, never()).translateText(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should use default language when RequestContextHolder is null")
    void getCurrentLanguage_WhenRequestContextHolderIsNull_ShouldUseDefaultLanguage() {
        // Arrange
        RequestContextHolder.resetRequestAttributes();

        when(hotelRepository.findAll()).thenReturn(hotels);
        when(hotelMapper.toDto(any(List.class))).thenReturn(hotelResponseDTOs);

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(hotelRepository).findAll();
        verify(hotelMapper).toDto(any(List.class));
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

        List<HotelResponseDTO> motelResponseDTOs = new ArrayList<>();
        motelResponseDTOs.add(motelResponseDTO);

        when(hotelRepository.findAll()).thenReturn(motelHotels);
        when(hotelMapper.toDto(motelHotels)).thenReturn(motelResponseDTOs);

        // Setting to English language
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getHeader("Accept-Language")).thenReturn("en");

        // Act
        List<HotelResponseDTO> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Motel", result.get(0).getName());
        assertEquals("This is the hotel Motel", result.get(0).getDescription());

        verify(hotelRepository).findAll();
        verify(hotelMapper).toDto(motelHotels);

        // Clean up
        RequestContextHolder.resetRequestAttributes();
    }
}
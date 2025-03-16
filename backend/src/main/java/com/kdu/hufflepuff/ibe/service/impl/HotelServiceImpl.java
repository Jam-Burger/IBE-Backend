package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidRequestException;
import com.kdu.hufflepuff.ibe.mapper.HotelMapper;
import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import com.kdu.hufflepuff.ibe.model.entity.HotelTranslation;
import com.kdu.hufflepuff.ibe.repository.HotelRepository;
import com.kdu.hufflepuff.ibe.repository.HotelTranslationRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;


@Service("hotelService")
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final HotelTranslationRepository translationRepository;
    private final TranslationService translationService;
    private final HotelMapper hotelMapper;

    public List<HotelResponseDTO> getAllHotels() {
        String language = getCurrentLanguage();
        List<Hotel> hotels = hotelRepository.findAll();

        if (!"en".equalsIgnoreCase(language)) {
            hotels.forEach(hotel -> {
                if (hotel.getTranslations() != null) {
                    hotel.getTranslations().stream()
                            .filter(translation -> translation.getLanguage().equalsIgnoreCase(language))
                            .findFirst()
                            .ifPresent(translation -> {
                                hotel.setName(translation.getTranslatedName());
                                hotel.setDescription(translation.getTranslatedDescription());
                            });
                }
            });
        }
        return hotelMapper.toDto(hotels);
    }

    public HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO) {
        if (hotelRequestDTO.getName() == null || hotelRequestDTO.getName().isEmpty()) {
            throw new InvalidRequestException("Hotel name cannot be empty");
        }

        // Use MapStruct to convert DTO to Entity
        Hotel hotel = hotelMapper.toEntity(hotelRequestDTO);

        // Save entity
        Hotel savedHotel = hotelRepository.save(hotel);

        // List of languages for translation
        List<String> languages = List.of("fr");

        for (String lang : languages) {
            String translatedName = translationService.translateText(hotel.getName(), "en", lang);
            String translatedDescription = translationService.translateText(hotel.getDescription(), "en", lang);

            HotelTranslation translation = new HotelTranslation();
            translation.setHotel(savedHotel);
            translation.setLanguage(lang);
            translation.setTranslatedName(translatedName);
            translation.setTranslatedDescription(translatedDescription);
            translationRepository.save(translation);
        }

        // Convert and return as DTO using MapStruct
        return hotelMapper.toDto(savedHotel);
    }

    private String getCurrentLanguage() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Accept-Language") != null ? request.getHeader("Accept-Language") : "en";
        }
        return "en";
    }
}


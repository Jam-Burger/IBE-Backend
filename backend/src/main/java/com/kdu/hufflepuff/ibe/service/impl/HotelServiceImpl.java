package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.InvalidRequestException;
import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import com.kdu.hufflepuff.ibe.model.entity.HotelTranslation;
import com.kdu.hufflepuff.ibe.repository.HotelRepository;
import com.kdu.hufflepuff.ibe.repository.HotelTranslationRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service("hotelService")
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final HotelTranslationRepository translationRepository;
    private final TranslationService translationService;
    private final ModelMapper modelMapper;

    public List<HotelResponseDTO> getAllHotels() {
        String language = getCurrentLanguage();
        List<Hotel> hotels = hotelRepository.findAll();

        if (!"en".equalsIgnoreCase(language)) {
            hotels.forEach(hotel -> {
                if (hotel.getTranslations() != null) {
                    log.info("Hotel translations found: " + hotel.getTranslations().size());
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
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel, HotelResponseDTO.class))
                .toList();
    }

    public HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO) {
        if (hotelRequestDTO.getName() == null || hotelRequestDTO.getName().isEmpty()) {
            throw new InvalidRequestException("Hotel name cannot be empty");
        }

        // Use ModelMapper to convert DTO to Entity
        Hotel hotel = modelMapper.map(hotelRequestDTO, Hotel.class);

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

        // Convert and return as DTO using ModelMapper
        return modelMapper.map(savedHotel, HotelResponseDTO.class);
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


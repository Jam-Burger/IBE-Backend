package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingSummaryDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.BookingExtension;
import com.kdu.hufflepuff.ibe.model.graphql.Booking;
import com.kdu.hufflepuff.ibe.model.graphql.Promotion;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Component that maps booking entities to DTOs using ModelMapper.
 */
@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ModelMapper modelMapper;

    public BookingDetailsDTO toBookingDetailsDTO(Booking booking, BookingExtension bookingExtension) {
        if (booking == null) {
            return null;
        }

        BookingDetailsDTO dto = BookingDetailsDTO.builder()
            .bookingId(booking.getBookingId())
            .checkInDate(booking.getCheckInDate())
            .checkOutDate(booking.getCheckOutDate())
            .adultCount(booking.getAdultCount())
            .childCount(booking.getChildCount())
            .totalCost(booking.getTotalCost())
            .amountDueAtResort(booking.getAmountDueAtResort())
            .bookingStatus(booking.getBookingStatus() != null ? booking.getBookingStatus().getStatus() : null)
            .propertyId(booking.getPropertyBooked() != null ? booking.getPropertyBooked().getPropertyId() : null)
            .roomNumbers(booking.getRoomBooked() != null ? booking.getRoomBooked().stream()
                .map(ra -> ra.getRoom().getRoomNumber())
                .distinct()
                .toList() : null)
            .roomTypeId(
                booking.getRoomBooked() != null ? booking.getRoomBooked().getFirst().getRoom().getRoomTypeId()
                    : null)
            .build();

        if (booking.getPromotionApplied() != null) {
            Promotion promotion = booking.getPromotionApplied();
            SpecialOfferResponseDTO offer = SpecialOfferResponseDTO.builder()
                .propertyId(dto.getPropertyId())
                .title(promotion.getPromotionTitle())
                .description(promotion.getPromotionDescription())
                .discountPercentage((1 - promotion.getPriceFactor()) * 100)
                .build();
            dto.setSpecialOffer(offer);
        }

        if (bookingExtension != null) {
            dto.setTransaction(bookingExtension.getTransaction());
            dto.setGuestDetails(bookingExtension.getGuestDetails());
            if (bookingExtension.getSpecialOffer() != null) {
                dto.setSpecialOffer(modelMapper.map(bookingExtension.getSpecialOffer(), SpecialOfferResponseDTO.class));
            }
        }
        return dto;
    }

    public BookingSummaryDTO mapToBookingSummaryDTO(Booking booking) {
        BookingSummaryDTO dto = new BookingSummaryDTO();

        dto.setBookingId(booking.getBookingId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setAdultCount(booking.getAdultCount());
        dto.setChildCount(booking.getChildCount());
        dto.setTotalCost(booking.getTotalCost());

        if (booking.getPropertyBooked() != null) {
            dto.setPropertyName(booking.getPropertyBooked().getPropertyName());
            dto.setPropertyAddress(booking.getPropertyBooked().getPropertyAddress());
            dto.setContactNumber(booking.getPropertyBooked().getContactNumber());
        }

        if (booking.getBookingStatus() != null) {
            dto.setStatus(booking.getBookingStatus().getStatus());
        }
        return dto;
    }
}
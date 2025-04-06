package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import com.kdu.hufflepuff.ibe.model.entity.BookingExtension;
import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.model.graphql.Booking;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.repository.jpa.BookingExtensionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingService;
import com.kdu.hufflepuff.ibe.service.interfaces.GuestExtensionService;
import com.kdu.hufflepuff.ibe.service.interfaces.PaymentService;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomAvailabilityService;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomLockService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import com.kdu.hufflepuff.ibe.util.GuestCountConverter;
import com.kdu.hufflepuff.ibe.util.PaymentMapper;
import com.kdu.hufflepuff.ibe.util.GuestCountConverter.GuestCounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingExtensionRepository bookingExtensionRepository;
    private final GuestExtensionService guestExtensionService;
    private final GraphQlClient graphQlClient;
    private final PaymentService paymentService;
    private final RoomAvailabilityService roomAvailabilityService;
    private final RoomLockService roomLockService;
    private final Random random = new Random();

    @Override
    @Transactional
    public Booking createBooking(BookingRequestDTO request) {
        // Step 1: Check room availability
        List<RoomAvailability> roomAvailabilities = roomAvailabilityService.fetchAvailableRooms(
            request.getPropertyId(), request.getDateRange().getFrom(), request.getDateRange().getTo());

        if (roomAvailabilities.size() < request.getRoomCount()) {
            throw new IllegalArgumentException("Not enough rooms available");
        }

        // Step 2: Select random rooms from available ones
        List<Long> selectedRoomIds = selectRandomRooms(roomAvailabilities, request.getRoomCount());

        // Step 3: Create room date locks
        roomLockService.createTemporaryLocks(selectedRoomIds, request.getDateRange().getFrom(), request.getDateRange().getTo());

        // Step 4: Process payment
        PaymentDTO payment = PaymentMapper.fromFormDataWithValidation(request.getFormData());
        long amount = calculateTotalAmount(request);
        String transactionId = paymentService.processPayment(payment, amount);
        log.info("Payment processed successfully. Transaction ID: {}", transactionId);

        // Step 5: Create guest extension
        GuestExtension guestExtension = guestExtensionService.createGuestExtension(request.getFormData());

        // Step 6: Extract guest counts
        GuestCounts guestCounts = GuestCountConverter.extractGuestCounts(request.getGuests());

        // Step 7: Create booking in GraphQL
        Booking booking = createBookingInGraphQL(request, guestCounts, selectedRoomIds);

        // Step 8: Create booking extension
        BookingExtension bookingExtension = BookingExtension.builder()
            .transactionId(transactionId)
            .guestDetails(guestExtension)
            .build();
        bookingExtension.setId(booking.getBookingId());
        bookingExtensionRepository.save(bookingExtension);

        return booking;
    }

    private List<Long> selectRandomRooms(List<RoomAvailability> availableRooms, int count) {
        return random.ints(0, availableRooms.size())
            .distinct()
            .limit(count)
            .mapToObj(availableRooms::get)
            .map(room -> room.getRoom().getRoomId())
            .toList();
    }

    private Booking createBookingInGraphQL(BookingRequestDTO request, GuestCounts guestCounts, List<Long> roomIds) {
        return graphQlClient.document(GraphQLQueries.CREATE_BOOKING)
            .variable("checkInDate", request.getDateRange().getFrom())
            .variable("checkOutDate", request.getDateRange().getTo())
            .variable("adultCount", guestCounts.adults)
            .variable("childCount", guestCounts.children)
            .variable("totalCost", 0)
            .variable("amountDueAtResort", 0)
            .variable("propertyId", request.getPropertyId())
            .variable("promotionId", request.getPromotionId())
            .variable("roomIds", roomIds)
            .retrieve("createBooking")
            .toEntity(Booking.class)
            .block();
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return null;
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        return null;
    }

    private long calculateTotalAmount(BookingRequestDTO request) {
        return 0;
    }
}
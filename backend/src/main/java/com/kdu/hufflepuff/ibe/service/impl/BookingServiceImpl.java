package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.*;
import com.kdu.hufflepuff.ibe.mapper.BookingMapper;
import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.BookingExtension;
import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import com.kdu.hufflepuff.ibe.model.entity.Transaction;
import com.kdu.hufflepuff.ibe.model.graphql.Booking;
import com.kdu.hufflepuff.ibe.model.graphql.Guest;
import com.kdu.hufflepuff.ibe.model.graphql.RoomAvailability;
import com.kdu.hufflepuff.ibe.repository.jpa.BookingExtensionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.*;
import com.kdu.hufflepuff.ibe.util.*;
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
    private final GuestService guestService;
    private final GraphQlClient graphQlClient;
    private final PaymentService paymentService;
    private final RoomAvailabilityService roomAvailabilityService;
    private final RoomLockService roomLockService;
    private final SpecialOfferService specialOfferService;
    private final OTPService otpService;
    private final BookingMapper bookingMapper;
    private final DelayedEmailSchedulerService delayedEmailSchedulerService;
    private final Random random = new Random();

    @Override
    @Transactional
    public BookingDetailsDTO createBooking(Long tenantId, BookingRequestDTO request, String otp) {
        String travelerEmail = request.getFormData().get("travelerEmail");
        if (travelerEmail == null || travelerEmail.isEmpty()) {
            throw BookingOperationException.guestCreationFailed("Traveler email is required");
        }

        // Step 0: Validate OTP
        if (!otpService.isOTPVerified(travelerEmail, otp)) {
            throw OTPException.invalidOtp("Invalid OTP provided");
        }

        // Step 1: Check room availability and select rooms
        RoomSelectionResult roomSelection = selectRoomsForBooking(request);
        List<Long> selectedRoomIds = roomSelection.roomIds();
        List<Long> selectedAvailabilityIds = roomSelection.availabilityIds();

        // Step 2: Create temporary locks on selected rooms
        applyRoomLocks(selectedRoomIds, request.getDateRange().getFrom(), request.getDateRange().getTo());

        try {
            // Step 3: Process payment
            Transaction transaction = processPaymentForBooking(request);

            // Step 4: Check for existing guest or create new one
            GuestExtension guestExtension = guestService.findByEmail(travelerEmail);
            Guest guest;

            if (guestExtension != null) {
                guest = guestService.findGuestById(guestExtension.getId());
                if (guest == null) {
                    String fullName = guestExtension.getTravelerFirstName() + " " + guestExtension.getTravelerLastName();
                    guest = guestService.createGuestWithId(fullName, guestExtension.getId());
                }
            } else {
                String firstName = request.getFormData().get("travelerFirstName");
                String lastName = request.getFormData().get("travelerLastName");
                String fullName = firstName + " " + lastName;

                guest = guestService.createGuest(fullName);
                guestExtension = guestService.createGuestExtensionWithId(request.getFormData(), guest.getGuestId());
            }

            // Step 5: Create the booking
            GuestCounts guestCounts = GuestCountConverter.extractGuestCounts(request.getGuests());
            Long propertyId = roomSelection.propertyId();
            Booking booking = createBookingInGraphQL(request, guestCounts, propertyId, guest.getGuestId());

            // Step 6: Update room availabilities to connect to the booking
            updateRoomAvailabilitiesForBooking(selectedAvailabilityIds, booking.getBookingId());

            // Step 7: Create booking extension and apply promotions
            createBookingExtension(booking.getBookingId(), transaction, guestExtension, request.getPromotionId());

            // Step 8: delete OTP
            otpService.deleteOtp(otp);

            // Step 9: add email to queue
            delayedEmailSchedulerService.scheduleBookingConfirmationEmail(
                travelerEmail,
                guestExtension.getTravelerFirstName() + " " + guestExtension.getTravelerLastName(),
                booking.getBookingId(),
                propertyId,
                tenantId
            );

            // Step 10: return booking details
            return getBookingDetailsById(booking.getBookingId());
        } catch (Exception e) {
            throw BookingOperationException.bookingCreationFailed("Unexpected error during booking creation: " + e.getMessage(), e);
        } finally {
            releaseRoomLocks(selectedRoomIds, request.getDateRange().getFrom(), request.getDateRange().getTo());
        }
    }

    @Override
    @Transactional
    public BookingDetailsDTO cancelBooking(Long bookingId, String otp) {
        try {
            Booking booking = validateBookingForCancellation(bookingId);
            BookingExtension bookingExtension = bookingExtensionRepository.findByBookingId(bookingId);

            if (bookingExtension == null) {
                throw BookingOperationException.bookingNotFound(bookingId);
            }

            String travelerEmail = bookingExtension.getGuestDetails().getTravelerEmail();
            if (travelerEmail == null || travelerEmail.isEmpty()) {
                throw BookingOperationException.guestCreationFailed("Traveler email is required");
            }

            if (!otpService.isOTPVerified(travelerEmail, otp)) {
                throw OTPException.invalidOtp("Invalid OTP provided");
            }

            updateBookingStatusToCancelled(bookingId);

            otpService.deleteOtp(otp);
            return bookingMapper.toBookingDetailsDTO(booking, bookingExtension);
        } catch (Exception e) {
            throw BookingOperationException.bookingCancellationFailed(bookingId);
        }
    }

    /**
     * Checks room availability and selects rooms for booking.
     */
    private RoomSelectionResult selectRoomsForBooking(BookingRequestDTO request) {
        List<RoomAvailability> roomAvailabilities = fetchAndValidateRoomAvailability(
            request.getRoomTypeId(),
            request.getDateRange().getFrom(),
            request.getDateRange().getTo(),
            request.getRoomCount()
        );

        List<Long> selectedRoomIds = selectRandomRoomIds(roomAvailabilities, request.getRoomCount());
        if (selectedRoomIds.isEmpty()) {
            throw RoomAvailabilityException.noRoomsAvailableForCount(request.getRoomCount());
        }

        List<Long> selectedAvailabilityIds = extractAvailabilityIds(roomAvailabilities, selectedRoomIds);
        Long propertyId = roomAvailabilities.getFirst().getProperty().getPropertyId();
        return new RoomSelectionResult(selectedRoomIds, selectedAvailabilityIds, propertyId);
    }

    /**
     * Fetches available rooms and validates there are enough for the booking.
     */
    private List<RoomAvailability> fetchAndValidateRoomAvailability(
        Long roomTypeId, LocalDate startDate, LocalDate endDate, int roomCount) {
        List<RoomAvailability> roomAvailabilities = roomAvailabilityService
            .fetchAvailableRoomsByRoomTypeId(roomTypeId, startDate, endDate);

        if (roomAvailabilities.size() < roomCount) {
            throw RoomAvailabilityException.notEnoughRooms(roomCount, roomAvailabilities.size());
        }

        return roomAvailabilities;
    }

    /**
     * Extracts availability IDs based on selected room IDs.
     */
    private List<Long> extractAvailabilityIds(List<RoomAvailability> availabilities, List<Long> roomIds) {
        return availabilities.stream()
            .filter(ra -> roomIds.contains(ra.getRoom().getRoomId()))
            .map(RoomAvailability::getAvailabilityId)
            .toList();
    }

    /**
     * Applies temporary locks to the selected rooms.
     */
    private void applyRoomLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        roomLockService.createTemporaryLocks(roomIds, startDate, endDate);
    }

    /**
     * Releases locks on rooms in case of failure.
     */
    private void releaseRoomLocks(List<Long> roomIds, LocalDate startDate, LocalDate endDate) {
        try {
            roomLockService.releaseLocks(roomIds, startDate, endDate);
        } catch (Exception e) {
            log.error("Failed to release locks for rooms: {}", roomIds, e);
        }
    }

    /**
     * Processes payment for the booking.
     */
    private Transaction processPaymentForBooking(BookingRequestDTO request) {
        PaymentDTO payment = PaymentMapper.fromFormDataWithValidation(request.getFormData());
        Double amount = paymentService.calculateDueNowAmount(request.getTotalAmount());

        try {
            return paymentService.processPayment(payment, amount);
        } catch (Exception e) {
            throw PaymentException.paymentFailed(e.getMessage(), e);
        }
    }

    /**
     * Updates room availabilities to be associated with a booking.
     */
    private void updateRoomAvailabilitiesForBooking(List<Long> availabilityIds, Long bookingId) {
        for (Long availabilityId : availabilityIds) {
            try {
                RoomAvailability updatedAvailability = roomAvailabilityService.updateRoomAvailability(availabilityId, bookingId);
                log.info("Updated room availability: {}", updatedAvailability);
            } catch (Exception e) {
                throw BookingOperationException.updateAvailabilityFailed(availabilityId, bookingId);
            }
        }
    }

    /**
     * Creates booking extension and applies promotions if provided.
     */
    private BookingExtension createBookingExtension(
        Long bookingId, Transaction transaction, GuestExtension guestExtension, String promotionId) {
        BookingExtension bookingExtension = BookingExtension.builder()
            .transaction(transaction)
            .bookingId(bookingId)
            .guestDetails(guestExtension)
            .build();

        if (promotionId != null && promotionId.startsWith("R_")) {
            applySpecialOffer(bookingExtension, promotionId);
        }

        return bookingExtensionRepository.save(bookingExtension);
    }

    /**
     * Applies promotion to booking if valid.
     */
    private void applySpecialOffer(BookingExtension bookingExtension, String promotionId) {
        try {
            Long specialOfferId = Long.parseLong(promotionId.replace("R_", ""));
            SpecialOffer specialOffer = specialOfferService.getSpecialOfferById(specialOfferId);

            if (specialOffer != null) {
                bookingExtension.setSpecialOffer(specialOffer);
            } else {
                throw PromotionException.specialOfferNotFound(specialOfferId);
            }
        } catch (NumberFormatException e) {
            throw PromotionException.invalidPromotionFormat(promotionId);
        }
    }

    private List<Long> selectRandomRoomIds(List<RoomAvailability> availableRooms, int count) {
        List<Long> allRoomIds = availableRooms.stream()
            .map(roomAvailability -> roomAvailability.getRoom().getRoomId())
            .distinct()
            .toList();

        if (allRoomIds.size() < count) {
            return List.of();
        }

        return random.ints(0, allRoomIds.size())
            .distinct()
            .limit(count)
            .mapToObj(allRoomIds::get)
            .toList();
    }

    private Booking createBookingInGraphQL(BookingRequestDTO request, GuestCounts guestCounts,
                                           Long propertyId, Long guestId) {
        try {
            boolean hasGqlPromotion = request.getPromotionId() != null && request.getPromotionId().startsWith("G_");
            GraphQlClient.RequestSpec requestSpec = buildBookingGraphQLRequest(
                request, guestCounts, propertyId, guestId, hasGqlPromotion);

            Booking booking = requestSpec
                .retrieve("createBooking")
                .toEntity(Booking.class)
                .block();

            log.info("Booking created: {}", booking);
            return booking;
        } catch (Exception e) {
            throw BookingOperationException.bookingCreationFailed("Failed to create booking in GraphQL: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the GraphQL request for booking creation.
     */
    private GraphQlClient.RequestSpec buildBookingGraphQLRequest(
        BookingRequestDTO request, GuestCounts guestCounts,
        Long propertyId, Long guestId, boolean hasGqlPromotion) {

        GraphQlClient.RequestSpec requestSpec = graphQlClient.document(GraphQLMutations.getCreateBookingQuery(hasGqlPromotion))
            .variable("checkInDate", DateFormatUtils.toGraphQLDateString(request.getDateRange().getFrom()))
            .variable("checkOutDate", DateFormatUtils.toGraphQLDateString(request.getDateRange().getTo()))
            .variable("adultCount", guestCounts.adults)
            .variable("childCount", guestCounts.children)
            .variable("totalCost", Math.round(request.getTotalAmount()))
            .variable("amountDueAtResort", Math.round(request.getTotalAmount() - paymentService.calculateDueNowAmount(request.getTotalAmount())))
            .variable("propertyId", propertyId)
            .variable("guestId", guestId);

        if (hasGqlPromotion) {
            Long realPromotionId = Long.parseLong(request.getPromotionId().replace("G_", ""));
            requestSpec = requestSpec.variable("promotionId", realPromotionId);
        }

        return requestSpec;
    }

    /**
     * Validates if a booking can be cancelled.
     */
    private Booking validateBookingForCancellation(Long bookingId) {
        Booking currentBooking = fetchBookingFromGraphQL(bookingId);
        if (currentBooking.getBookingStatus() == null || currentBooking.getBookingStatus().getStatus().equals("CANCELLED")) {
            throw BookingOperationException.bookingAlreadyCancelled(bookingId);
        }
        return currentBooking;
    }

    /**
     * Updates booking status to cancelled in GraphQL.
     */
    private void updateBookingStatusToCancelled(Long bookingId) {
        try {
            graphQlClient.document(GraphQLMutations.UPDATE_BOOKING_STATUS)
                .variable("bookingId", bookingId)
                .variable("statusId", 2)
                .retrieve("updateBooking")
                .toEntity(Booking.class)
                .block();
        } catch (Exception e) {
            throw BookingOperationException.bookingCancellationFailed(bookingId);
        }
    }

    @Override
    public BookingDetailsDTO getBookingDetailsById(Long bookingId) {
        Booking booking = fetchBookingFromGraphQL(bookingId);
        BookingExtension bookingExtension = bookingExtensionRepository.findByBookingId(bookingId);
        log.info("Booking: {}", booking);
        log.info("Booking extension: {}", bookingExtension);
        return bookingMapper.toBookingDetailsDTO(booking, bookingExtension);
    }

    /**
     * Fetches booking data from GraphQL.
     */
    private Booking fetchBookingFromGraphQL(Long bookingId) {
        try {
            Booking booking = graphQlClient.document(GraphQLQueries.GET_BOOKING_DATA)
                .variable("bookingId", bookingId)
                .retrieve("getBooking")
                .toEntity(Booking.class)
                .block();

            if (booking == null) {
                throw BookingOperationException.bookingNotFound(bookingId);
            }

            log.info("Retrieved booking: {}", booking.getBookingId());
            return booking;
        } catch (Exception e) {
            throw BookingOperationException.bookingNotFound(bookingId);
        }
    }

    private record RoomSelectionResult(List<Long> roomIds, List<Long> availabilityIds, Long propertyId) {
    }
}